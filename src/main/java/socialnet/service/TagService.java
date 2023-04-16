package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.model.Post2Tag;
import socialnet.model.Tag;
import socialnet.repository.Post2TagRepository;
import socialnet.repository.TagRepository;

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

    public List<Post2Tag> getPostByQueryTags(String[] tags){
        List<Tag> tagList = tagRepository.getTagsByQuery(tags);
        if (!tagList.isEmpty()) {
            return post2TagRepository.getQuery(tagList);
        } else {
            return null;
        }
    }
}
