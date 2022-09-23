package com.example.demorestapi.events;

import com.example.demorestapi.common.ErrorsResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

        // TODO 일반적인 Bad_Request 처리
        // TODO build() -> 에러 메시지 body(errors)에 담기
        if(errors.hasErrors()){
            //return ResponseEntity.badRequest().body(errors);
            return badRequest(errors);
        }

        // TODO 특정한 값 Bad_Request 처리
        // TODO build() -> 에러 메시지 body(errors)에 담기
        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        // TODO Model Mapper 사용 후 입력값 제한하기
        Event event = modelMapper.map(eventDto, Event.class);

        // TODO 저장하기 전에 유료인지 무료인지 여부 업데이트(비즈니스 로직 적용)
        event.update();

        // TODO DB에 ID가 있는지 확인
        Event newEvent = this.eventRepository.save(event);

        // TODO HATEOAS 적용-1
        // TODO Location 헤더 정보에 URI 담기
        // TODO ControllerLinkBuilder(2.1.0.RELEASE) -> WebMvcLinkBuilder(2.2.5.RELEASE)
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        // TODO HATEOAS 적용-2
        //링크 추가
        EntityModel<Event> eventResource = EventResource.modelOf(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        // TODO _links.self(EventResource로 옮김)
//        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(Link.of("/docs/api.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> pagedResources = assembler.toModel(page, e-> EventResource.modelOf(e));
        //var pagedResources = assembler.toModel(page, e -> new EventResource(e));
        pagedResources.add(Link.of("/docs/api.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if( optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EntityModel<Event> eventResource = EventResource.modelOf(event);
        eventResource.add(Link.of("/docs/api.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity putEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if( optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event event = optionalEvent.get();
        this.modelMapper.map(eventDto, event);
        Event savedEvent = this.eventRepository.save(event);

        EntityModel<Event> eventResource = EventResource.modelOf(savedEvent);
        eventResource.add(Link.of("/docs/api.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);


    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
    }
}
