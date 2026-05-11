package myproject.common.utils;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
@EnableScheduling
public class Tasks {
//    @Resource
//    private YiyaoService yiyaoService;

//    @Scheduled(cron = "0 0 0/1 * * ?")
//	@Scheduled(cron = "0 */1 * * * ?")
    public void testTasks() {
//        System.out.println("定时任务开始执行" + System.currentTimeMillis());
//        yiyaoService.configureTasks();
    }
}
