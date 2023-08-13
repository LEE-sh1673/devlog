package com.devlog.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.domain.Comment;
import com.devlog.domain.Post;
import com.devlog.errors.v1.NotFoundException;
import com.devlog.errors.v2.PasswordMisMatchesException;
import com.devlog.repository.comment.CommentRepository;
import com.devlog.repository.post.PostRepository;
import com.devlog.service.dto.CommentCreateDto;
import com.devlog.service.dto.CommentDeleteDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder encoder;

    public void write(final Long postId, final CommentCreateDto requestDto) {
        final Post post = postRepository.findById(postId)
            .orElseThrow(NotFoundException::new);
        final Comment comment = modelMapper.map(requestDto, Comment.class);
        comment.updatePassword(encoder.encode(requestDto.getPassword()));
        post.addComment(comment);
        commentRepository.save(comment);
    }

    public void delete(final Long commentId, final CommentDeleteDto requestDto) {
        final Comment comment = commentRepository.findById(commentId)
            .orElseThrow(NotFoundException::new);

        final String encodedPassword = comment.getPassword();

        if (!encoder.matches(requestDto.getPassword(), encodedPassword)) {
            throw new PasswordMisMatchesException();
        }
        commentRepository.delete(comment);
    }
}
