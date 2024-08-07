package com.techbank.account.common.infrastructure;

import com.techbank.account.common.domain.BaseEntity;
import com.techbank.account.common.queries.BaseQuery;
import com.techbank.account.common.queries.QueryHandlerMethod;

import java.util.List;

public interface QueryDispatcher {
    <T extends BaseQuery> void registerHandler(Class<T> type, QueryHandlerMethod<T> handler);
    <U extends BaseEntity> List<U> send(BaseQuery query);
}
