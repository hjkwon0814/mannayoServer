package hansung.mannayo.mannayoserverapplication.Service;

import hansung.mannayo.mannayoserverapplication.Model.Entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Long getCountCommentByBoardId(Long id);

    Optional<List<Comment>> getCommentByBoardId(Long id);

    Boolean setComment(Long memberid, Long boardid, String contents);

    Optional<Comment> findCommentById(Long id);
}
