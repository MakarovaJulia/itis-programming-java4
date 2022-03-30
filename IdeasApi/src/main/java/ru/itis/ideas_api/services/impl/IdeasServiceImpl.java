package ru.itis.ideas_api.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.server.ResponseStatusException;
import ru.itis.ideas_api.dto.IdeaDto;
import ru.itis.ideas_api.exceptions.ErrorEntity;
import ru.itis.ideas_api.exceptions.ValidationException;
import ru.itis.ideas_api.mapper.IdeasMapper;
import ru.itis.ideas_api.model.Idea;
import ru.itis.ideas_api.model.User;
import ru.itis.ideas_api.repository.IdeasRepository;
import ru.itis.ideas_api.repository.UsersRepository;
import ru.itis.ideas_api.services.IdeasService;
import ru.itis.ideas_api.services.validation.IdeaDtoValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdeasServiceImpl implements IdeasService {
    private final UsersRepository usersRepository;
    private final IdeasRepository ideasRepository;
    private final IdeasMapper ideasMapper;
    private final IdeaDtoValidator ideaDtoValidator;

    @Override
    public IdeaDto saveIdea(IdeaDto ideaDto) {
        DataBinder dataBinder = new DataBinder(ideaDto);
        dataBinder.addValidators(ideaDtoValidator);
        dataBinder.validate();

        if(dataBinder.getBindingResult().hasErrors()) {
            ObjectError error = dataBinder.getBindingResult().getAllErrors().get(0);
            throw new ValidationException((ErrorEntity) error.getArguments()[0]);
        }

        Optional<User> optionalUser = usersRepository.findById(ideaDto.getAuthorId());
        if(optionalUser.isPresent() == false) {
            // TODO: use custom exception
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("USER WITH ID %d NOT FOUND", ideaDto.getAuthorId()));
        }
        Idea idea = ideasMapper.getIdea(ideaDto);
        idea.setAuthor(optionalUser.get());
        Idea savedIdea = ideasRepository.save(idea);
        return ideasMapper.getDto(savedIdea);
    }

    @Override
    public IdeaDto getIdea(Long id) {
        Optional<Idea> optionalIdea = ideasRepository.findById(id);
        if(optionalIdea.isPresent() == false) {
            // TODO: use custom exception
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("IDEA WITH ID %d NOT FOUND", id));
        }
        return ideasMapper.getDto(optionalIdea.get());
    }

    @Override
    public List<IdeaDto> getAll() {
        return ideasRepository.findAll().stream()
                .map(ideasMapper::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (ideasRepository.findById(id).isPresent() == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("IDEA WITH ID %d NOT FOUND", id));
        }
        ideasRepository.deleteById(id);
    }
}
