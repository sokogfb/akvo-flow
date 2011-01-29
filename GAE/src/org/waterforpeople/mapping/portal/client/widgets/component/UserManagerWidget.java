package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.user.PermissionDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserService;
import org.waterforpeople.mapping.app.gwt.client.user.UserServiceAsync;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget used for creating/editing/searching users.
 * 
 * @author Christopher Fagiani
 * 
 */
public class UserManagerWidget extends Composite implements
		DataTableListener<UserDto>, DataTableBinder<UserDto>, ClickHandler {
	private static final String DEFAULT_SORT_FIELD = "username";
	private static final DataTableHeader[] GRID_HEADERS = {
			new DataTableHeader("User Name"),
			new DataTableHeader("Email Address"),
			new DataTableHeader("Permissions"), new DataTableHeader("") };
	private VerticalPanel contentPane;
	private PaginatedDataTable<UserDto> dataTable;
	private TextBox usernameField;
	private TextBox emailField;
	private Button searchButton;
	private Button addNewButton;
	private UserServiceAsync userService;
	private List<PermissionDto> permissionList;

	public UserManagerWidget() {
		contentPane = new VerticalPanel();
		contentPane.add(buildSearchHeader());
		dataTable = new PaginatedDataTable<UserDto>(DEFAULT_SORT_FIELD, this,
				this, false);
		contentPane.add(dataTable);
		initWidget(contentPane);

		userService = GWT.create(UserService.class);
		userService.listPermissions(new AsyncCallback<List<PermissionDto>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDia = new MessageDialog("Error",
						"Could not load permissions: "
								+ caught.getLocalizedMessage());
				errDia.showCentered();
			}

			@Override
			public void onSuccess(List<PermissionDto> result) {
				permissionList = result;
				requestData(null, false);
			}
		});
	}

	/**
	 * builds the controls at the top of the portlet used for searching
	 * 
	 * @return
	 */
	private Widget buildSearchHeader() {
		HorizontalPanel hPanel = new HorizontalPanel();
		Label l = new Label("Username: ");
		hPanel.add(l);
		usernameField = new TextBox();
		hPanel.add(usernameField);
		l = new Label("Email: ");
		hPanel.add(l);
		emailField = new TextBox();
		hPanel.add(emailField);
		searchButton = new Button("Search");
		searchButton.addClickHandler(this);
		hPanel.add(searchButton);
		addNewButton = new Button("Add User");
		addNewButton.addClickHandler(this);
		hPanel.add(addNewButton);

		return hPanel;
	}

	@Override
	public void onItemSelected(UserDto item) {
		// no-op

	}

	/**
	 * call the server to get more data
	 */
	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		userService.listUsers(null, null, null, null, cursor,
				new AsyncCallback<ResponseDto<ArrayList<UserDto>>>() {

					@Override
					public void onSuccess(ResponseDto<ArrayList<UserDto>> result) {
						dataTable.bindData(result.getPayload(), result
								.getCursorString(), isNew, isResort);
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog("Error",
								"There was an error while attempting to search for users: "
										+ caught.getMessage());
						errDia.show();
					}
				});
	}

	/**
	 * installs the data into the row on the data grid
	 */
	@Override
	public void bindRow(Grid grid, final UserDto item, int row) {
		final TextBox uBox = new TextBox();
		uBox.setText(item.getUserName());
		grid.setWidget(row, 0, uBox);
		final TextBox eBox = new TextBox();
		eBox.setText(item.getEmailAddress());
		grid.setWidget(row, 1, eBox);
		final ListBox permBox = constructPermissionBox(item);
		grid.setWidget(row, 2, permBox);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button saveButton = new Button("Save");
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				item.setUserName(uBox.getText());
				item.setEmailAddress(eBox.getText());
				item.setPermissionList(formPermissionString(permBox));
				userService.saveUser(item, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog("Error",
								"There was an error while attempting to save the user: "
										+ caught.getMessage());
						errDia.show();
					}

					@Override
					public void onSuccess(Void result) {
						MessageDialog confDia = new MessageDialog("User Saved",
								"The user has been updated");
						confDia.show();

					}
				});
			}
		});
		buttonPanel.add(saveButton);
		Button deleteButton = new Button("Delete");
		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				item.setUserName(uBox.getText());
				item.setEmailAddress(eBox.getText());
				userService.deleteUser(item.getKeyId(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										"Error",
										"There was an error while attempting to delete the user: "
												+ caught.getMessage());
								errDia.show();

							}

							@Override
							public void onSuccess(Void result) {
								MessageDialog confDia = new MessageDialog(
										"User Deleted", "User has been deleted");
								confDia.show();
								requestData(null, false);
							}
						});
			}
		});
		buttonPanel.add(deleteButton);
		grid.setWidget(row, 3, buttonPanel);

	}

	private ListBox constructPermissionBox(UserDto u) {
		ListBox lb = new ListBox(true);
		if (permissionList != null) {
			for (int i = 0; i < permissionList.size(); i++) {
				lb.addItem(permissionList.get(i).getName(), permissionList.get(
						i).getCode());
				if (u != null
						&& u.hasPermission(permissionList.get(i).getCode())) {
					lb.setItemSelected(i, true);
				}
			}
		}
		return lb;
	}

	/**
	 * forms a comma delimited string of permission codes using what is selected
	 * in the box
	 * 
	 * @param box
	 * @return
	 */
	private String formPermissionString(ListBox box) {
		StringBuilder buf = new StringBuilder();
		if (box != null) {
			int count = 0;
			for (int i = 0; i < box.getItemCount(); i++) {
				if (box.isItemSelected(i)) {
					if (count > 0) {
						buf.append(",");
					}
					buf.append(box.getValue(i));
					count++;
				}
			}
		}
		return buf.toString();
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return GRID_HEADERS;
	}

	/**
	 * handles the search and add new methods
	 */
	@Override
	public void onClick(ClickEvent event) {
		String userName = usernameField.getText().trim();
		String email = emailField.getText().trim();
		if (event.getSource() == addNewButton) {
			if (userName.length() == 0 || email.length() == 0) {
				MessageDialog errDia = new MessageDialog(
						"Missing Mandatory Data",
						"Please enter both email address and username.");
				errDia.showCentered();
			} else {
				UserDto u = new UserDto();
				u.setUserName(userName);
				u.setEmailAddress(email);
				userService.saveUser(u, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errorDia = new MessageDialog(
								"Could not save user",
								"There was an error while trying to save the user: "
										+ caught.getMessage());
						errorDia.showCentered();
					}

					@Override
					public void onSuccess(Void result) {
						MessageDialog confDia = new MessageDialog("User Saved",
								"User has been saved");
						confDia.showCentered();
						usernameField.setText("");
						emailField.setText("");
						requestData(null, false);
					}
				});
			}

		} else if (event.getSource() == searchButton) {
			userService.listUsers(userName.length() > 0 ? userName : null,
					email.length() > 0 ? email : null, null, null, null,
					new AsyncCallback<ResponseDto<ArrayList<UserDto>>>() {

						@Override
						public void onFailure(Throwable caught) {
							MessageDialog errorDia = new MessageDialog(
									"Could not search",
									"There was an error while trying to search for usesr: "
											+ caught.getMessage());
							errorDia.showCentered();
						}

						@Override
						public void onSuccess(
								ResponseDto<ArrayList<UserDto>> result) {
							dataTable.bindData(result.getPayload(), result
									.getCursorString(), true, false);
						}
					});
		}
	}
}
