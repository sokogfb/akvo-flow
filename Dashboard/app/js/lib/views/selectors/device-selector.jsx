import React from 'react';

import Checkbox from 'akvo-flow/components/Checkbox';
/* eslint-disable jsx-a11y/label-has-for */
require('akvo-flow/views/react-component');
/* eslint-disable jsx-a11y/click-events-have-key-events */
FLOW.DeviceGroupSelectorView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
    this.deviceGroups = {};
    this.deviceGroupNames = {};
    this.handleChange = this.handleChange.bind(this);
    this.deviceInAssignment = this.deviceInAssignment.bind(this);
    this.renderDevices = this.renderDevices.bind(this);
  },

  didInsertElement(...args) {
    this._super(...args);
    if (FLOW.deviceGroupControl.content.isLoaded) {
      FLOW.deviceGroupControl.get('content').forEach((item) => {
        this.deviceGroupNames[item.get('keyId')] = item.get('code');
        this.deviceGroups[item.get('keyId')] = {}; // initialize array of devices per group
      });
      if (FLOW.deviceControl.content.isLoaded) {
        FLOW.selectedControl.selectedSurveyAssignment.get('devices').forEach((deviceId) => {
          // populate pre-selected devices
          const device = FLOW.Device.find(deviceId);
          if (device && device.get('keyId')) {
            FLOW.selectedControl.selectedDevices.pushObject(device);
          }
        });
        FLOW.deviceControl.get('content').forEach((device) => {
          this.deviceGroups[device.get('deviceGroup') ? device.get('deviceGroup') : 1][device.get('keyId')] = {
            name: device.get('deviceIdentifier'),
            checked: this.deviceInAssignment(device.get('keyId')),
          };
        });
      }
      this.renderDevices();
    }
  },

  renderDevices() {
    this.reactRender(
      <div className="formSelectorList">
        {Object.keys(this.deviceGroups).map(dgId => (
          <div key={dgId}>
            <div className="accordion" onClick={this.deviceGroupClick}>
              {/* Object values accessible only by sqaure braces */}
              {this.deviceGroupNames[dgId]}
            </div>
            <div className="panel">
              {Object.keys(this.deviceGroups[dgId]).map(deviceId => (
                <div key={deviceId}>
                  <Checkbox
                    id={deviceId}
                    name={deviceId}
                    onChange={this.handleChange}
                    checked={this.deviceGroups[dgId][deviceId].checked}
                  />
                  <label htmlFor={deviceId}>
                    {this.deviceGroups[dgId][deviceId].name}
                  </label>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    );
  },

  deviceInAssignment(deviceId) {
    const devicesInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get('devices');
    return devicesInAssignment.indexOf(deviceId) > -1;
  },

  handleChange(e) {
    const deviceId = e.target.name;
    const clickedDevice = FLOW.deviceControl.get('content').find(device => device.get('keyId') == deviceId);
    const groupId = clickedDevice && clickedDevice.get('deviceGroup') ? clickedDevice.get('deviceGroup') : 1;
    const currentStatus = this.deviceGroups[groupId][deviceId].checked;
    this.deviceGroups[groupId][deviceId].checked = !currentStatus;
    this.renderDevices();
    // add/remove device to/from assignment
    if (this.deviceGroups[groupId][deviceId].checked) {
      // push device to FLOW.selectedControl.selectedDevices
      FLOW.selectedControl.selectedDevices.pushObject(FLOW.Device.find(deviceId));
    } else {
      FLOW.selectedControl.selectedDevices.removeObject(FLOW.Device.find(deviceId));
    }
  },

  deviceGroupClick(e) {
    /* Toggle between adding and removing the "active" class,
    to highlight the button that controls the panel */
    e.target.classList.toggle('active');

    /* Toggle between hiding and showing the active panel */
    let panel = e.target.nextElementSibling;
    if (panel.style.display === 'block') {
      panel.style.display = 'none';
    } else {
      panel.style.display = 'block';
    }
  },

  comparator(a, b) {
    const nameA = a.name.toUpperCase(); // ignore upper and lowercase
    const nameB = b.name.toUpperCase(); // ignore upper and lowercase
    if (nameA < nameB) {
      return -1;
    }
    if (nameA > nameB) {
      return 1;
    }

    // names must be equal
    return 0;
  },
});
