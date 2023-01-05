package com.linkyb.chatapp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController // 데이터를 리턴하는 서버
public class ChatController {

    private final ChatRepository chatRepository;

    //일대일 대화 시에 사용
    @CrossOrigin
    @GetMapping(value = "/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMsgs(@PathVariable String sender, @PathVariable String receiver) {
        return chatRepository.mFindBySender(sender, receiver)
                .subscribeOn(Schedulers.boundedElastic());
    }

    //방 별로 채팅
    @CrossOrigin
    @GetMapping(value = "/chat/roomNum/{roomNum}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> findByRoomNum(@PathVariable Integer roomNum) {
        return chatRepository.nFindByRoomNum(roomNum)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @CrossOrigin
    @PostMapping("/chat")
    public Mono<Chat> setMsg(@RequestBody Chat chat) {
        chat.setCreatedAt(LocalDateTime.now());
        return chatRepository.save(chat); // Object를 리턴하면 자동으로 JSON 변환(by MessageConverter)
    }
}
