package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.model.Post2Tag;
import socialnet.model.Tag;
import socialnet.repository.Post2TagRepository;
import socialnet.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final Post2TagRepository post2TagRepository;

    public void createTags(List<String> tagsStrings, int postId) {
        for (String tagsString : tagsStrings) {
            long tagId = tagRepository.save(new Tag(tagsString), postId);
            post2TagRepository.save(new Post2Tag((long) postId, tagId));
        }
    }

    public String getPostByQueryTags(String[] tags){
        List<Tag> tagList = tagRepository.getTagsByQuery(tags);
        List<Post2Tag> query;
        if (tagList != null) {
            query = post2TagRepository.getQuery(tagList);
        } else {
            return null;
        }
        StringBuilder sqlTags = new StringBuilder(" ");
        if (query != null && !query.isEmpty()) {
            for (Post2Tag post2Tag : query) {
                if (post2Tag.getPostId() != 0) {
                    sqlTags.append(" ").append(post2Tag.getId()).append(", ");
                }
            }
            if (sqlTags.length() > 4) {
                if (sqlTags.substring(sqlTags.length() - 2).equals(", ")) {
                    sqlTags.delete(sqlTags.length() - 2, sqlTags.length());
                }
            }
            return sqlTags.toString();
        } else {
            return null;
        }
    }
}
