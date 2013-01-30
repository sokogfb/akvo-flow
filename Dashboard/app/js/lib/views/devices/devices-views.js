
FLOW.CurrentDevicesTabView = Ember.View.extend({
// FLOW.CurrentDevicesTabView = FLOW.View.extend({
	showDeleteDevicesDialogBool: false,
	showAddToGroupDialogBool: false,
	showRemoveFromGroupDialogBool: false,
	showManageDeviceGroupsDialogBool: false,
	newDeviceGroupName: null,
	// bound to devices-list.handlebars
	changedDeviceGroupName: null,
	selectedDeviceGroup: null,
	// bound to devices-list.handlebars
	showAddToGroupDialog: function() {
		this.set('selectedDeviceGroup', null);
		this.set('showAddToGroupDialogBool', true);
	},

	showRemoveFromGroupDialog: function() {
		this.set('showRemoveFromGroupDialogBool', true);
	},

	cancelAddToGroup: function() {
		this.set('showAddToGroupDialogBool', false);
	},

	showManageDeviceGroupsDialog: function() {
		this.set('newDeviceGroupName', null);
		this.set('changedDeviceGroupName', null);
		this.set('selectedDeviceGroup', null);
		this.set('showManageDeviceGroupsDialogBool', true);
	},

	cancelManageDeviceGroups: function() {
		this.set('showManageDeviceGroupsDialogBool', false);
	},

	assignDisplayNames: function() {
		var allDevices, deviceId, deviceGroup, combinedName;
		if((FLOW.deviceControl.content.get('isLoaded') === true) && (FLOW.deviceGroupControl.content.get('isLoaded') === true)) {
			allDevices = FLOW.store.filter(FLOW.Device, function(data) {
				return true;
			});
			allDevices.forEach(function(item) {
				if(Ember.empty(item.get('deviceIdentifier'))) {
					combinedName = "no identifer";
				} else {
					combinedName = item.get('deviceIdentifier');
				}
				item.set('combinedName', combinedName + " " + item.get('phoneNumber'));
				deviceId = parseInt(item.get('deviceGroup'), 10);
				deviceGroup = FLOW.store.filter(FLOW.DeviceGroup, function(data) {
					return(data.get('keyId') === deviceId);
				});
				if(typeof deviceGroup.get('firstObject') !== "undefined") {
					item.set('deviceGroupName', deviceGroup.get('firstObject').get('code'));
				} else {
					item.set('deviceGroupName', 'unassigned');
				}
			});
		}
	}.observes('FLOW.deviceGroupControl.content.isLoaded', 'FLOW.deviceControl.content.isLoaded', 'FLOW.deviceControl.content.isUpdated'),

	doAddToGroup: function() {
		if(this.get('selectedDeviceGroup') !== null) {
			var selectedDeviceGroupId = this.selectedDeviceGroup.get('keyId');
			var selectedDeviceGroupName = this.selectedDeviceGroup.get('code');
			var selectedDevices = FLOW.store.filter(FLOW.Device, function(data) {
				if(data.get('isSelected') === true) {
					return true;
				} else {
					return false;
				}
			});
			selectedDevices.forEach(function(item) {
				item.set('deviceGroupName', selectedDeviceGroupName);
				item.set('deviceGroup', selectedDeviceGroupId);
			});
		}
		FLOW.store.commit();
		this.set('showAddToGroupDialogBool', false);
	},

	// TODO repopulate list after update
	doRemoveFromGroup: function() {
		var selectedDevices = FLOW.store.filter(FLOW.Device, function(data) {
			if(data.get('isSelected') === true) {
				return true;
			} else {
				return false;
			}
		});
		selectedDevices.forEach(function(item) {
			item.set('deviceGroupName', null);
			item.set('deviceGroup', null);
		});

		FLOW.store.commit();
		this.set('showRemoveFromGroupDialogBool', false);
	},

	cancelRemoveFromGroup: function() {
		this.set('showRemoveFromGroupDialogBool', false);
	},

	copyDeviceGroupName: function() {
		if(this.get('selectedDeviceGroup') !== null) {
			this.set('changedDeviceGroupName', this.selectedDeviceGroup.get('code'));
		}
	}.observes('this.selectedDeviceGroup'),

	// TODO update device group name in tabel.
	doManageDeviceGroups: function() {
		var allDevices;
		if(this.get('selectedDeviceGroup') !== null) {
			var selectedDeviceGroupId = this.selectedDeviceGroup.get('keyId');

			// this could have been changed in the UI
			var originalSelectedDeviceGroup = FLOW.store.find(FLOW.DeviceGroup, selectedDeviceGroupId);

			if(originalSelectedDeviceGroup.get('code') != this.get('changedDeviceGroupName')) {
				var newName = this.get('changedDeviceGroupName');
				originalSelectedDeviceGroup.set('code', newName);

				allDevices = FLOW.store.filter(FLOW.Device, function(data) {
					return true;
				});
				allDevices.forEach(function(item) {
					if(parseInt(item.get('deviceGroup'), 10) == selectedDeviceGroupId) {
						item.set('deviceGroupName', newName);
					}
				});
			}
		} else if(this.get('newDeviceGroupName') !== null) {
			FLOW.store.createRecord(FLOW.DeviceGroup, {
				"code": this.get('newDeviceGroupName')
			});
		}

		this.set('selectedDeviceGroup', null);
		this.set('newDeviceGroupName', null);
		this.set('changedDeviceGroupName', null);

		FLOW.store.commit();
		this.set('showManageDeviceGroupsDialogBool', false);
	},

	showDeleteDevicesDialog: function() {
		console.log("show delete devices dialog");
	},

	doDeleteDevices: function() {

	},

	cancelDeleteDevices: function() {

	}
});


// TODO not used?
FLOW.SavingDeviceGroupView = FLOW.View.extend({
	showDGSavingDialogBool: false,

	showDGSavingDialog: function() {
		if(FLOW.DeviceGroupControl.get('allRecordsSaved')) {
			this.set('showDGSavingDialogBool', false);
		} else {
			this.set('showDGSavingDialogBool', true);
		}
	}.observes('FLOW.deviceGroupControl.allRecordsSaved')
});