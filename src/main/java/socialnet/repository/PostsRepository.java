package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.dto.CommentRs;
import socialnet.mapper.PostMapper;
import socialnet.dto.PostRs;
import socialnet.model.*;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostsRepository{
    private final JdbcTemplate jdbcTemplate;




}
