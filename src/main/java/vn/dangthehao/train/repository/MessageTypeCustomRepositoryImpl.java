package vn.dangthehao.train.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import oracle.jdbc.OracleConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.MsgTypeSummary;

import java.sql.*;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Repository
public class MessageTypeCustomRepositoryImpl implements MessageTypeCustomRepository {
  JdbcTemplate jdbcTemplate;

  @Override
  public String batchCreateMessageType(List<MsgTypeSummary> msgTypeSummaryList) {
    return jdbcTemplate.execute(
        (Connection con) -> {
          var oracleCon = con.unwrap(OracleConnection.class);
          Struct[] structArray = new Struct[msgTypeSummaryList.size()];
          for (int i = 0; i < msgTypeSummaryList.size(); i++) {
            MsgTypeSummary msgTypeSummary = msgTypeSummaryList.get(i);
            Object[] attributes =
                new Object[] {
                  msgTypeSummary.getMsgType(),
                  msgTypeSummary.getDescription(),
                  msgTypeSummary.getActiveStatus()
                };
            structArray[i] = oracleCon.createStruct("MSG_TYPE_OBJ", attributes);
          }

          Array oracleArray = oracleCon.createOracleArray("MSG_TYPE_LIST", structArray);
          CallableStatement cs = con.prepareCall("{call BATCH_INSERT_MSG_TYPE(?, ?)}");
          cs.setArray(1, oracleArray);
          cs.registerOutParameter(2, Types.VARCHAR);

          return cs;
        },
        (CallableStatement cs) -> {
          cs.execute();
          return (String) cs.getObject(2);
        });
  }
}
