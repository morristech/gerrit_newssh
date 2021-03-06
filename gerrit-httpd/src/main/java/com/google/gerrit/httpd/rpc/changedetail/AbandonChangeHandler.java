// Copyright (C) 2009 The Android Open Source Project
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

package com.google.gerrit.httpd.rpc.changedetail;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ReviewResult;
import com.google.gerrit.common.errors.NoSuchEntityException;
import com.google.gerrit.httpd.rpc.Handler;
import com.google.gerrit.reviewdb.client.PatchSet;
import com.google.gerrit.server.changedetail.AbandonChange;
import com.google.gerrit.server.mail.EmailException;
import com.google.gerrit.server.patch.PatchSetInfoNotAvailableException;
import com.google.gerrit.server.project.InvalidChangeOperationException;
import com.google.gerrit.server.project.NoSuchChangeException;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.IOException;

import javax.annotation.Nullable;

class AbandonChangeHandler extends Handler<ChangeDetail> {

  interface Factory {
    AbandonChangeHandler create(PatchSet.Id patchSetId, String message);
  }

  private final Provider<AbandonChange> abandonChangeProvider;
  private final ChangeDetailFactory.Factory changeDetailFactory;

  private final PatchSet.Id patchSetId;
  @Nullable
  private final String message;

  @Inject
  AbandonChangeHandler(final Provider<AbandonChange> abandonChangeProvider,
      final ChangeDetailFactory.Factory changeDetailFactory,
      @Assisted final PatchSet.Id patchSetId,
      @Assisted @Nullable final String message) {
    this.abandonChangeProvider = abandonChangeProvider;
    this.changeDetailFactory = changeDetailFactory;

    this.patchSetId = patchSetId;
    this.message = message;
  }

  @Override
  public ChangeDetail call() throws NoSuchChangeException, OrmException,
      EmailException, NoSuchEntityException, InvalidChangeOperationException,
      PatchSetInfoNotAvailableException, RepositoryNotFoundException,
      IOException {
    final AbandonChange abandonChange = abandonChangeProvider.get();
    abandonChange.setChangeId(patchSetId.getParentKey());
    abandonChange.setMessage(message);
    final ReviewResult result = abandonChange.call();
    if (result.getErrors().size() > 0) {
      throw new NoSuchChangeException(result.getChangeId());
    }
    return changeDetailFactory.create(result.getChangeId()).call();
  }
}
