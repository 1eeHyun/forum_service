package com.ldh.forum.board.service;

import com.ldh.forum.board.model.Board;
import com.ldh.forum.board.repository.BoardRepository;
import com.ldh.forum.user.model.User;
import com.ldh.forum.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public LikeService(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void toggleLike(Long boardId, String username) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Post does not exist."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));

        if (board.getLikedUsers().contains(user)) {
            board.getLikedUsers().remove(user);
            board.setLikes(board.getLikes() - 1);
        } else {
            board.getLikedUsers().add(user);
            board.setLikes(board.getLikes() + 1);
        }

        boardRepository.save(board);
    }
}
