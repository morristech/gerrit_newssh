// Copyright (C) 2010 The Android Open Source Project
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

package com.google.gerrit.client.changes;

import com.google.gerrit.client.Gerrit;
import com.google.gerrit.reviewdb.client.AccountGeneralPreferences;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtjsonrpc.common.AsyncCallback;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtjsonrpc.common.VoidResult;

abstract class DownloadCommandLink extends Anchor implements ClickHandler {
  final AccountGeneralPreferences.DownloadCommand cmdType;

  DownloadCommandLink(AccountGeneralPreferences.DownloadCommand cmdType,
      String text) {
    super(text);
    this.cmdType = cmdType;
    setStyleName(Gerrit.RESOURCES.css().downloadLink());
    Accessibility.setRole(getElement(), Accessibility.ROLE_TAB);
    addClickHandler(this);
  }

  @Override
  public void onClick(ClickEvent event) {
    event.preventDefault();
    event.stopPropagation();

    select();

    if (Gerrit.isSignedIn()) {
      // If the user is signed-in, remember this choice for future panels.
      //
      AccountGeneralPreferences pref =
          Gerrit.getUserAccount().getGeneralPreferences();
      pref.setDownloadCommand(cmdType);
      com.google.gerrit.client.account.Util.ACCOUNT_SVC.changePreferences(pref,
          new AsyncCallback<VoidResult>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(VoidResult result) {
            }
          });
    }
  }

  public AccountGeneralPreferences.DownloadCommand getCmdType() {
    return cmdType;
  }

  void select() {
    DownloadCommandPanel parent = (DownloadCommandPanel) getParent();
    for (Widget w : parent) {
      if (w != this && w instanceof DownloadCommandLink) {
        w.removeStyleName(Gerrit.RESOURCES.css().downloadLink_Active());
      }
    }
    parent.setCurrentCommand(this);
    addStyleName(Gerrit.RESOURCES.css().downloadLink_Active());
  }

  abstract void setCurrentUrl(DownloadUrlLink link);
}
