package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import socialnet.model.Post2Tag;
import socialnet.model.Tag;
import socialnet.repository.Post2TagRepository;
import socialnet.repository.TagRepository;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final Post2TagRepository post2TagRepository;

    public String getPostByQueryTags(String[] tags) {
        List<Long> tagsId = tagRepository.getTagsIdByName(Arrays.asList(tags));
        return StringUtils.join(tagsId, ",");

        /*List<Tag> tagList = tagRepository.getTagsByQuery(tags);
        List<Post2Tag> query;
        if (!tagList.isEmpty()) {
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

            if (sqlTags.length() > 4 && (sqlTags.substring(sqlTags.length() - 2).equals(", "))) {
                sqlTags.delete(sqlTags.length() - 2, sqlTags.length());
            }

            return sqlTags.toString();
        } else {
            return null;
        }*/
    }
}
