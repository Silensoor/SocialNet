package socialnet.service;

import liquibase.repackaged.org.apache.commons.lang3.tuple.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.dto.*;
import socialnet.mapper.PostCommentMapper;
import socialnet.mapper.PostMapper;
import socialnet.model.*;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;
    private final PersonService authorService;
    private final CommentService commentService;
    private final JwtUtils jwtUtils;
    private final PostCommentRepository postCommentRepository;
    private final PostMapper postMapper;
    private final PostCommentMapper postCommentMapper;
    private final TagService tagService;


    public Pair<Integer, List<PostRs>> getAllPosts(Integer offset, Integer perPage) {
        List<Post> posts = postRepository.findAll();
        List<PostRs> result = new ArrayList<>();
        for (Post post : posts) {
            PostRs postRs = convertToPostRs(post);
            result.add(postRs);
        }

        return Pair.of(result.size(), result.stream().skip(offset).limit(perPage).collect(Collectors.toList()));
    }

    public PostRs convertToPostRs(Post post) {
        PostRs postRs = new PostRs();
        Person person = personRepository.findById(post.getAuthorId());
        PersonRs personRs =  authorService.convertToPersonRs(person);
        postRs.setAuthor(personRs);
        List<Comment> comments = commentRepository.findByPostId(post.getId());
        List<CommentRs> commentsRs = comments.stream().map(commentService::convertToCommentRs).collect(Collectors.toList());
        postRs.setComments(commentsRs);
        postRs.setId(post.getId());
        postRs.setIsBlocked(post.getIsBlocked());
        long likesCount = likeRepository.findCountByPersonId(person.getId());
        postRs.setLikes(likesCount);
        postRs.setMyLike(null);
        postRs.setPostText(post.getPostText());
        List<String> tags = tagRepository.findByPostId(post.getId());
        postRs.setTags(tags);
        postRs.setTime(post.getTime().toString());
        postRs.setTitle(post.getTitle());
        postRs.setType(null);

        return postRs;
    }

    public CommonRs<List<PostRs>> getFeeds(String jwtToken, int offset, int perPage) {
        List<Post> posts = postRepository.findAll();
        List<PostRs> postRsList = new ArrayList<>();
        for (Post post : posts) {
            int postId = (int) post.getId();
            Details details = getDetails(post.getAuthorId(), postId, jwtToken);
            PostRs postRs = postMapper.toRs(post, details);
            postRsList.add(postRs);
        }
        postRsList.sort(Comparator.comparing(PostRs::getTime));
        return new CommonRs<>(postRsList, perPage, offset, perPage, (int) System.currentTimeMillis(), postRsList.size());
    }

    private Details getDetails(long authorId, int postId, String jwtToken) {
        Person author = getAuthor(authorId);
        List<Like> likes = getLikes(postId);
        List<Tag> tags = getTags(postId);
        List<String> tagsStrings = tags.stream().map(Tag::getTag).collect(Collectors.toList());
        Person authUser = getAuthUser(jwtToken);
        List<PostComment> postComments = getPostComments(postId);
        List<CommentRs> comments = getComments(postComments, jwtToken);
        return new Details(author, likes, tagsStrings, authUser.getId(), comments);
    }

    private List<CommentRs> getComments(List<PostComment> postComments, String jwtToken) {
        List<CommentRs> comments = new ArrayList<>();
        for (PostComment postComment : postComments) {
            int commentId = postComment.getId().intValue();
            Person author = getAuthor(postComment.getAuthorId());
            List<PostComment> subCommentsList = getPostComments(commentId);
            List<CommentRs> subComments = getComments(subCommentsList, jwtToken);
            List<Like> likes = getLikes(commentId);
            Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
            long authUserId = authUser.getId();
            CommentRs commentRs = postCommentMapper.toDTO(author, postComment, subComments, likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    public CommonRs<PostRs> createPost(PostRq postRq, int id, Integer publishDate, String jwtToken) {
        personRepository.findById(id);
        Post post = postMapper.toModel(postRq, publishDate, id);
        int postId = postRepository.save(post);
        tagService.createTags(postRq.getTags(), postId);
        Person author = personRepository.findById(id);
        Details details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = postMapper.toRs(post,details);
        return new CommonRs<>(postRs, (int) System.currentTimeMillis());
    }

    public CommonRs<PostRs> getPostById(int postId, String jwtToken) {
        Post post = postRepository.findById(postId);
        Person author = getAuthor(post.getAuthorId());
        Details details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = postMapper.toRs(post, details);
        return new CommonRs<>(postRs, (int) System.currentTimeMillis());
    }

    private Person getAuthor(long id) {
        return personRepository.findById((int) id);
    }
    private List<Like> getLikes(int id) {
        return likeRepository.getLikesByEntityId(id);
    }
    private List<Tag> getTags(int id) {
        return tagRepository.getTagsByPostId(id);
    }
    private Person getAuthUser(String jwtToken) {

        Person person = new Person();
        person.setId(1L);
        return person;
    }
    private List<PostComment> getPostComments(int id) {
        return postCommentRepository.getCommentsByPostId(id);
    }

    public CommonRs<PostRs> updatePost(int id, PostRq postRq, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        int publishDate = (int) postFromDB.getTime().getTime();
        Post post = postMapper.toModel(postRq, publishDate, id);
        postRepository.updateById(id, post);
        Post newPost = postRepository.findById(id);
        Person author = getAuthor(newPost.getId());
        List<Like> likes = getLikes((int) newPost.getId());
        List<Tag> tags = getTags((int) newPost.getId());
        List<PostComment> postComments = getPostComments((int) newPost.getId());
        List<CommentRs> comments = getComments(postComments, jwtToken);
        Details details = getDetails(author.getId(), (int) newPost.getId(), jwtToken);
        PostRs postRs = postMapper.toRs(newPost, details);
        return new CommonRs<>(postRs, (int) System.currentTimeMillis());
    }

    public CommonRs<PostRs> markAsDelete(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setIsDeleted(true);
        postRepository.updateById(id, postFromDB);
        Person author = getAuthor(postFromDB.getId());
        Details details = getDetails(author.getId(), (int) postFromDB.getId(), jwtToken);
        PostRs postRs = postMapper.toRs(postFromDB, details);
        return new CommonRs<>(postRs,(int) System.currentTimeMillis());
    }

    public CommonRs<PostRs> recoverPost(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setIsDeleted(false);
        Person author = getAuthor(postFromDB.getId());
        Details details = getDetails(author.getId(), (int) postFromDB.getId(), jwtToken);
        PostRs postRs = postMapper.toRs(postFromDB, details);
        return new CommonRs<>(postRs,(int) System.currentTimeMillis());
    }

    public CommonRs<List<PostRs>> getPostsByQuery(String jwtToken, String author, Integer dateFrom, Integer dateTo, int offset, int perPage, String[] tags, String text) {
        List<PostRs> postRsList = getFeeds(jwtToken, offset, perPage).getData();
        List<PostRs> tempPostRsList = new ArrayList<>();
        if (author != null) {
            for (PostRs postRs : postRsList) {
                String name = postRs.getAuthor().getLastName() + " " + postRs.getAuthor().getFirstName();
                if (name.contains(author)) continue;
                tempPostRsList.add(postRs);
            }
        }
        if (dateFrom != null) {
            for (PostRs postRs : postRsList) {
                if (Timestamp.valueOf(postRs.getTime()).after(new Timestamp(dateFrom))) continue;
                tempPostRsList.add(postRs);
            }
        }
        if (dateTo != null) {
            for (PostRs postRs : postRsList) {
                if (Timestamp.valueOf(postRs.getTime()).before(new Timestamp(dateTo))) continue;
                tempPostRsList.add(postRs);
            }
        }
        if (tags != null) {
            for (PostRs postRs : postRsList) {
                if (postRs.getTags().containsAll(Arrays.stream(tags).collect(Collectors.toList()))) continue;
                tempPostRsList.add(postRs);
            }
        }
        if (text != null) {
            for (PostRs postRs : postRsList) {
                if (postRs.getPostText().contains(text)) continue;
                tempPostRsList.add(postRs);
            }
        }
        postRsList.removeAll(tempPostRsList);
        return new CommonRs<>(postRsList, perPage, offset, perPage, (int) System.currentTimeMillis(), postRsList.size());
    }

    @Data
    @AllArgsConstructor
    public class Details {
        Person author;
        List<Like> likes;
        List<String> tags;
        Long authUserId;
        List<CommentRs> comments;
    }
}
