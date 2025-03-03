package com.dss.auditlog.view.user;

import com.dss.auditlog.entity.User;
import com.dss.auditlog.view.mainviewtopmenu.MainViewTopMenu;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "users", layout = MainViewTopMenu.class)
@ViewController("AL_User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView extends StandardListView<User> {

}