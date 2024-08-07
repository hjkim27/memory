```java
// [CronTrigger] ==========
CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("fixRemoteSessionTrigger", "default")
        .withSchedule(cronScheduleBuilder)
        .forJob("fixRemoteSessionJob", "default")
        .build();
JobDetail jobDetail = JobBuilder.newJob(FixRemoteSessionJob.class).withIdentity(trigger.getJobKey()).build();
scheduleTrigger(trigger, jobDetail);

// [SimpleTrigger] ==========
JobDataMap jobDataMap = new JobDataMap();
jobDataMap.put("policyId", pcScanPolicy.getId());
Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity(triggerName, triggerGroup)
        .startAt(cal.getTime())
        .forJob(jobName, jobGroup)
        .usingJobData(jobDataMap)
        .build();
JobDetail jobDetail = JobBuilder.newJob(PolicyJob.class).withIdentity(trigger.getJobKey()).build();    
scheduleTrigger(trigger, jobDetail);
```
