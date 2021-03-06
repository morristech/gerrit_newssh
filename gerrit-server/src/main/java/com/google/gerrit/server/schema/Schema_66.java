// Copyright (C) 2012 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.server.schema;

import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gwtorm.jdbc.JdbcSchema;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.sql.SQLException;
import java.sql.Statement;

public class Schema_66 extends SchemaVersion {

  @Inject
  Schema_66(Provider<Schema_65> prior) {
    super(prior);
  }

  @Override
  protected void migrateData(ReviewDb db, UpdateUI ui)
      throws OrmException, SQLException {
    final Statement stmt = ((JdbcSchema) db).getConnection().createStatement();
    try {
      stmt.executeUpdate("UPDATE accounts SET reverse_patch_set_order = 'Y' "+
                         "WHERE display_patch_sets_in_reverse_order = 'Y'");
      stmt.executeUpdate("UPDATE accounts SET show_username_in_review_category = 'Y' " +
                         "WHERE display_person_name_in_review_category = 'Y'");
    } finally {
      stmt.close();
    }
  }
}
