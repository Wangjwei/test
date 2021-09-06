package com.example.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test.entity.User;
import com.example.test.service.ElasticService;
import com.example.test.service.UserService;
import com.example.test.service.mapper.UserMapper;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.FuzzyOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wangjw
 * @since 2021-09-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private ElasticService elasticService;
    @Autowired
    private UserService userService;
    @Value("${elasticsearch.indexName}")
    private String indexName;


    @Override
    public void insertUser() throws Exception {
        List<User> userList = userService.list();
        userList.forEach(user -> elasticService.insertOrUpdateOne(indexName,user));
    }

    @Override
    public List<String> searchCompletionSuggest(String searchValue) {
        String field = "address";
        if (checkLetter(searchValue)) {
            field = "address.pinyin";
        }
        //用SearchSourceBuilder来构造查询请求体
        List<String> result = new ArrayList<>();
        result = elasticService.searchCompletionSuggest(indexName, getSearchSourceBuilder(field, searchValue, false));
        if (result.size() == 0  && checkLetter(searchValue)) {
            //拼音fuzzy查询
            result = elasticService.searchCompletionSuggest(indexName, getSearchSourceBuilder(field, searchValue, true));
            if (result.size() == 0) {
                //首字母查询
                result = elasticService.searchCompletionSuggest(indexName,
                        getSearchSourceBuilder("address.pinyin", searchValue, false));
            }
        }
        return result;
    }

    /**
     * getCompletionSuggestionBuilder
     *
     * @param field
     * @param value
     * @return
     */
    private SuggestionBuilder getCompletionSuggestionBuilder(String field, String value, Boolean isFuzzy) {
        if (isFuzzy) {
            return SuggestBuilders.completionSuggestion(field).prefix(value,
                    FuzzyOptions.builder().setFuzziness(2).build()).skipDuplicates(true).size(10);
        } else {
            return SuggestBuilders.completionSuggestion(field).prefix(value).skipDuplicates(true).size(10);
        }
    }

    /**
     * getSearchSourceBuilder
     *
     * @param field
     * @param value
     * @param isFuzzy 是否fuzzy模糊查询
     * @return
     */
    private SearchSourceBuilder getSearchSourceBuilder(String field, String value, Boolean isFuzzy) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder completionSuggestionBuilder = getCompletionSuggestionBuilder(field, value, isFuzzy);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("search-suggest", completionSuggestionBuilder);
        sourceBuilder.suggest(suggestBuilder);
        return sourceBuilder;
    }

    /**
     * 只包含字母
     *
     * @return 验证成功返回true，验证失败返回false
     */
    private boolean checkLetter(String cardNum) {
        String regex = "^[A-Za-z]+$";
        return Pattern.matches(regex, cardNum);
    }

    /**
     * 验是否中文
     *
     * @param chinese 中文字符
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkChinese(String chinese) {
        String regex = "^[\u4E00-\u9FA5]+$";
        return Pattern.matches(regex, chinese);
    }
}
