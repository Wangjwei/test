package com.example.activiti;

import io.swagger.annotations.ApiOperation;
import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ActivitiApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @ApiOperation("生成数据库表结构")
    void createDatabase(){
        // 创建流程引擎配置信息对象
        /*ProcessEngineConfiguration pec = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration();
        // 设置数据库的类型
        pec.setDatabaseType("mysql");
        // 设置创建数据库的方式
//        ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE(true);//如果没有数据库表就会创建数据库表，有的话就修改表结构.
        // ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE(false): 不会创建数据库表
        // ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP(create-drop): 先创建、再删除.
        pec.setDatabaseSchemaUpdate("true");
        // 设置数据库驱动
        pec.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        // 设置jdbcURL
        pec.setJdbcUrl("jdbc:mysql://192.168.3.126:3306/activiti?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8");
        // 设置用户名
        pec.setJdbcUsername("s_root");
        // 设置密码
        pec.setJdbcPassword("1Q2w3e");

        // 构建流程引擎对象
        ProcessEngine pe = pec.buildProcessEngine(); // 调用访方法才会创建数据表
        // 调用close方法时，才会删除
        pe.close();*/
    }

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    @Test
    @ApiOperation("部署流程")
    public void deployProcess(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("WorkTestNew.bpmn");//bpmn文件的名称
        builder.deploy();
    }
    @ApiOperation("启动流程")
    @Test
    public void startProcess(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("myProcess_1");//流程的名称，对应流程定义表的key字段，也可以使用ByID来启动流程
    }
    @ApiOperation("查询流程")
    @Test
    public void queryTask(){
        TaskService taskService = processEngine.getTaskService();
        //根据assignee(节点接受人)查询任务
        String assignee = "张三";//
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();

        int size = tasks.size();
        for (int i = 0; i < size; i++) {
            Task task = tasks.get(i);

        }
        //首次运行的时候这个没有输出，因为第一次运行的时候扫描act_ru_task的表里面是空的，但第一次运行完成之后里面会添加一条记录，之后每次运行里面都会添加一条记录
        for (Task task : tasks) {
            System.out.println("taskId=" +"流程任务节点信息ID："+ task.getId() +
                    ",taskName:" +"流程任务节点名称ID：" +task.getName() +
                    ",assignee:" + "流程任务节点接受人："+task.getAssignee() +
                    ",createTime:" +"流程任务节点创建时间："+ task.getCreateTime());
        }
    }
    @ApiOperation("查询流程定义明细")
    @Test
    public void queryProcdef(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //创建查询对象
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        //添加查询条件
        query.processDefinitionKey("myProcess_1");//通过key获取
        // .processDefinitionName("My process")//通过name获取
        // .orderByProcessDefinitionId()//根据ID排序
        //执行查询获取流程定义明细
        List<ProcessDefinition> pds = query.list();
        for (ProcessDefinition pd : pds) {
            System.out.println("ID:"+pd.getId()+",NAME:"+pd.getName()+",KEY:"+pd.getKey()+",VERSION:"+pd.getVersion()+",RESOURCE_NAME:"+pd.getResourceName()+",DGRM_RESOURCE_NAME:"+pd.getDiagramResourceName());
        }
    }
    @ApiOperation("审核过程")
    @Test
    public void startProcessApproval(){
        TaskService taskService = processEngine.getTaskService();
        //taskId 就是查询任务中的 ID
        String taskId = "12502";
        //完成请假申请任务
        taskService.complete(taskId);
    }

}
