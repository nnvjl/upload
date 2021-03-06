/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clinbrain.bd.core.parse.filler.common.ddl.column;

import com.google.common.base.Optional;
import lombok.Setter;
import com.clinbrain.bd.core.metadata.table.ShardingTableMetaData;
import com.clinbrain.bd.core.parse.filler.api.SQLSegmentFiller;
import com.clinbrain.bd.core.parse.filler.api.ShardingTableMetaDataAwareFiller;
import com.clinbrain.bd.core.parse.sql.segment.ddl.column.ColumnDefinitionSegment;
import com.clinbrain.bd.core.parse.sql.segment.ddl.column.alter.ModifyColumnDefinitionSegment;
import com.clinbrain.bd.core.parse.sql.statement.SQLStatement;
import com.clinbrain.bd.core.parse.sql.statement.ddl.AlterTableStatement;

/**
 * Modify column definition filler.
 *
 * @author duhongjun
 */
@Setter
public final class ModifyColumnDefinitionFiller implements SQLSegmentFiller<ModifyColumnDefinitionSegment>, ShardingTableMetaDataAwareFiller {
    
    private ShardingTableMetaData shardingTableMetaData;
    
    @Override
    public void fill(final ModifyColumnDefinitionSegment sqlSegment, final SQLStatement sqlStatement) {
        AlterTableStatement alterTableStatement = (AlterTableStatement) sqlStatement;
        Optional<String> oldColumnName = sqlSegment.getOldColumnName();
        if (oldColumnName.isPresent()) {
            Optional<ColumnDefinitionSegment> oldColumnDefinition = alterTableStatement.findColumnDefinition(oldColumnName.get(), shardingTableMetaData);
            if (!oldColumnDefinition.isPresent()) {
                return;
            }
            oldColumnDefinition.get().setColumnName(sqlSegment.getColumnDefinition().getColumnName());
            if (null != sqlSegment.getColumnDefinition().getDataType()) {
                oldColumnDefinition.get().setDataType(sqlSegment.getColumnDefinition().getDataType());
            }
            alterTableStatement.getModifiedColumnDefinitions().put(oldColumnName.get(), oldColumnDefinition.get());
        } else {
            alterTableStatement.getModifiedColumnDefinitions().put(sqlSegment.getColumnDefinition().getColumnName(), sqlSegment.getColumnDefinition());
        }
        if (sqlSegment.getColumnPosition().isPresent()) {
            alterTableStatement.getChangedPositionColumns().add(sqlSegment.getColumnPosition().get());
        }
    }
}
