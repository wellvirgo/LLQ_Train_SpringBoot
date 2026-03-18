package vn.dangthehao.train.repository;

import vn.dangthehao.train.entity.MsgTypeSummary;

import java.util.List;

public interface MessageTypeCustomRepository {
    String batchCreateMessageType(List<MsgTypeSummary> msgTypeSummaryList);
}
