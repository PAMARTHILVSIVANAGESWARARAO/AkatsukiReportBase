package com.akatsuki_news_application.AkatsukiReportBase.source.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.akatsuki_news_application.AkatsukiReportBase.source.model.AkatsukiMember;
import com.akatsuki_news_application.AkatsukiReportBase.source.repository.AkatsukiMemberRepository;

@Component
public class AkatsukiMembersDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AkatsukiMembersDataInitializer.class);

    private final AkatsukiMemberRepository akatsukiMemberRepository;

    public AkatsukiMembersDataInitializer(AkatsukiMemberRepository akatsukiMemberRepository) {
        this.akatsukiMemberRepository = akatsukiMemberRepository;
    }

    @Override
    public void run(String... args) {
        // Insert constant members only if the table is empty
        if (akatsukiMemberRepository.count() == 0) {
            logger.info("Initializing Akatsuki members data...");

            akatsukiMemberRepository.save(new AkatsukiMember(
                    "obito",
                    "world",
                    "https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx1YlY4U0JXVnVMVWRDR2dKSlRpZ0FQAQ?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));

            akatsukiMemberRepository.save(new AkatsukiMember(
                    "kakuzu",
                    "business",
                    "https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx6TVdZU0JXVnVMVWRDR2dKSlRpZ0FQAQ?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));

            akatsukiMemberRepository.save(new AkatsukiMember(
                    "sasori",
                    "technology",
                    "https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGRqTVhZU0JXVnVMVWRDR2dKSlRpZ0FQAQ?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));

             akatsukiMemberRepository.save(new AkatsukiMember(
                    "deidara",
                    "entertainment",
                    "https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNREpxYW5RU0JXVnVMVWRDR2dKSlRpZ0FQAQ?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));

            akatsukiMemberRepository.save(new AkatsukiMember(
                    "kisame",
                    "sports",
                    "https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRFp1ZEdvU0JXVnVMVWRDR2dKSlRpZ0FQAQ?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));


             akatsukiMemberRepository.save(new AkatsukiMember(
                    "orichimaru",
                    "science",
                    "https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRFp0Y1RjU0JXVnVMVWRDR2dKSlRpZ0FQAQ?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));

             akatsukiMemberRepository.save(new AkatsukiMember(
                    "konan",
                    "health",
                    "https://news.google.com/topics/CAAqJQgKIh9DQkFTRVFvSUwyMHZNR3QwTlRFU0JXVnVMVWRDS0FBUAE?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));

            akatsukiMemberRepository.save(new AkatsukiMember(
                    "hidan",
                    "india",
                    "https://news.google.com/topics/CAAqJQgKIh9DQkFTRVFvSUwyMHZNRE55YXpBU0JXVnVMVWRDS0FBUAE?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));

            akatsukiMemberRepository.save(new AkatsukiMember(
                    "zetsu",
                    "world",
                    "https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx1YlY4U0JXVnVMVWRDR2dKSlRpZ0FQAQ?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));



             akatsukiMemberRepository.save(new AkatsukiMember(
                    "itachi",
                    "home",
                    "https://news.google.com/home?hl=en-IN&gl=IN&ceid=IN%3Aen"
            ));





            logger.info("Akatsuki members data initialized successfully with {} members.", akatsukiMemberRepository.count());
        } else {
            logger.info("Akatsuki members data already exists, skipping initialization.");
        }
    }
}

