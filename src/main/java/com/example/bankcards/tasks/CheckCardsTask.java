package com.example.bankcards.tasks;

import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckCardsTask {

    private final CardRepository cardRepository;
    @Scheduled(cron = "${task.cardexpiration.cron}")
    public void checkCardExpiration() {
        log.info("Checking expired cards");
        try{
            var cards = cardRepository.findByExpiredIn(LocalDate.now());

            cards.forEach(card -> {
                card.setStatus(CardStatus.EXPIRED);
            });

            if(cards.isEmpty()){
                return;
            }
            cardRepository.saveAll(cards);

            log.info("Check expired card successfully end");
        }catch (Exception e){
            log.error("Error while checking expired cards", e);
        }
    }

    @Scheduled(cron = "${task.blockbyrequest.cron}")
    public void blocksCardByUserRequest(){
        log.info("Block cards by user request");
        try{
            var cards = cardRepository.findByRequestNotIn(Set.of(CardStatus.BLOCKED, CardStatus.EXPIRED));

            cards.forEach(card -> {
                card.setBlockedRequestAt(null);
                card.setStatus(CardStatus.BLOCKED);
            });

            if(cards.isEmpty()){
                return;
            }

            cardRepository.saveAll(cards);

            log.info("Block cards by user request successfully end");
        }catch (Exception e){
            log.error("Error while blocking cards by user request", e);
        }
    }
}
