package com.sparta.storyindays.service;

import com.sparta.storyindays.dto.post.PostGetResDto;
import com.sparta.storyindays.dto.post.PostReqDto;
import com.sparta.storyindays.dto.post.PostResDto;
import com.sparta.storyindays.dto.post.PostUpdateResDto;
import com.sparta.storyindays.dto.user.Auth;
import com.sparta.storyindays.entity.Post;
import com.sparta.storyindays.entity.User;
import com.sparta.storyindays.enums.post.PostType;
import com.sparta.storyindays.jwt.JwtProvider;
import com.sparta.storyindays.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtProvider jwtProvider;

    public PostResDto writePost(PostReqDto reqDto, String accessToken) {
        // 유효한 JWT 토큰을 가진 본인, 인가된 유저인지 체크 jwtProvider

        // 유저를 레포지토리에서 찾아옴
        User user = new User("test2", "123", "lee2", Auth.USER, "test@email.com");
        // req, 찾아온 user로 게시글 entity 생성
        Post post = postRepository.save(reqDto.toPostEntity(user));
        // save 후 res dto로 반환
        PostResDto postReqDto = new PostResDto(post);

        return postReqDto;
    }

    public PostGetResDto getAllPost(int page, boolean isAsc) {

        Pageable pageable = getPageable(page, isAsc);

        // repository에서 공지글 찾아오기
        // repository에서 상단글 찾아오기
        // repository에서 일반글 찾아오기 (페이지), pageable
        PostGetResDto postGetResDto = new PostGetResDto(postRepository.findAllByPostType(PostType.NOTICE)
                , postRepository.findAllByPostTypeAndIsPinned(PostType.NORMAL, true)
                , postRepository.findAllByPostTypeAndIsPinned(PostType.NORMAL, false, pageable));

        postGetResDto.inputTestData();
        return postGetResDto;
    }

//    public PostGetResDto getUserPost(User user, int page, boolean isAsc) {
//        Pageable pageable = getPageable(page, isAsc);
//
//        // 유저를 레포지토리에서 찾아옴
//        User testuser = new User("test", "123", "lee", Auth.USER, "test@email.com");
//
//        PostGetResDto postGetResDto = new PostGetResDto(postRepository.findAllByPostType(PostType.NOTICE)
//                , postRepository.findAllByPostTypeAndIsPinned(PostType.NORMAL, true)
//                , postRepository.findAllByPostTypeAndIsPinnedAndUser(PostType.NORMAL, false, pageable, user));
//
//        postGetResDto.inputTestData();
//        return postGetResDto;
//    }

    public Pageable getPageable(int page, boolean isAsc) {
        // 정렬방향, 정렬 기준(생성일자 고정), 페이저블 생성
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, 5, sort);

        return pageable;
    }

    @Transactional
    public PostUpdateResDto updatePost(int postId, PostReqDto reqDto) {

        // 본인이 작성한 게시글의 수정인 경우만 인가하도록 조건걸기
        Post post = findById(postId);
        post.update(reqDto);
        return new PostUpdateResDto(post);
    }

    public Post findById(long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 id입니다"));
    }
}
