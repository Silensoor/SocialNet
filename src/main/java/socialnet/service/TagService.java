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
            long tagId = tagRepository.save(new Tag(tagsString));
            post2TagRepository.save(new Post2Tag((long) postId, tagId));
        }
    }

    public List<Post2Tag> getPostByQueryTags(String[] tags){
        StringBuilder sql = new StringBuilder("SELECT * FROM tags WHERE");
        for(Object tag : tags){
            if (tag != "" && tag != null) {
                sql.append(" tag = '").append(tag).append("' AND ");
            }
        }
        if (sql.substring(sql.length() - 5).equals("' AND ")){
            sql.substring(0, sql.length() - 5);
        }
        StringBuilder sql1 = new StringBuilder("SELECT * FROM post2tag WHERE");
        if (!sql.toString().equals("SELECT * FROM tags WHERE")) {
            List<Tag> tagList = tagRepository.getTagsByQuery(sql.toString());
            if (tagList != null && !tagList.isEmpty()) {

                for (Tag tag1 : tagList) {
                    sql1.append(" tag_id = '").append(tag1).append("' AND ");
                }
                if (sql1.substring(sql1.length() - 5).equals("' AND ")){
                    sql1 = new StringBuilder(sql1.substring(0, sql.length() - 5));
                }
            }
        }
        return post2TagRepository.getQuery(sql1.toString());
    }

//    List<Tag> getTags(int id) {
//        return tagRepository.getTagsByPostId(id);
//    }

}
