package com.redfish.moji_server;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.redfish.moji_server.repositories.MessageRepository;

@Component
public class ScheduledTasks {

    @Autowired
    private MessageRepository messageRepository;

    //private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	//private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	//@Scheduled(fixedRate = 5000)
	//public void reportCurrentTime() {
	//	log.info("The time is now {}", dateFormat.format(new Date()));
	//}

    @Scheduled(fixedRate = 60000) // Every minute
    //@Scheduled(fixedRate = 10000) // Every 10s
    public void deleteOldMessages() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        // LocalDateTime oneDayAgo = LocalDateTime.now().minusSeconds(10);
        messageRepository.deleteByTimeLessThan(oneDayAgo);
    }
}
