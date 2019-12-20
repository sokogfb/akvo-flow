/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import { groupBy as _groupBy } from 'lodash';
import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';

import AssignmentsContext from '../assignment-context';
import DeviceEmpty from '../__partials/DeviceEmpty';

export default class EditDevices extends React.Component {
  state = {
    selectedDevices: [],
  };

  getDeviceGroups() {
    // filter out selected devices
    const { devices, selectedDeviceIds } = this.context.data;

    const filteredDevices = devices.filter(device => selectedDeviceIds.includes(device.id));

    return _groupBy(filteredDevices, device => device.deviceGroup.id);
  }

  onSelectDevice = (id, checked) => {
    const { selectedDevices } = this.state;
    let newSelectedDevices = [];

    if (checked) {
      newSelectedDevices = selectedDevices.concat(id);
    } else {
      newSelectedDevices = selectedDevices.filter(device => device !== id);
    }

    this.setState({ selectedDevices: newSelectedDevices });
  };

  onSelectMultipleDevices = (ids, checked) => {
    const { selectedDevices } = this.state;
    let newSelectedDevices = [...selectedDevices];

    if (checked) {
      newSelectedDevices = selectedDevices.concat(ids);
    } else {
      newSelectedDevices = selectedDevices.filter(device => !ids.includes(device));
    }

    this.setState({
      selectedDevices: [...new Set(newSelectedDevices)],
    });
  };

  removeFromAssignment = () => {
    const { selectedDevices } = this.state;
    const { removeDevicesFromAssignment } = this.context.actions;

    removeDevicesFromAssignment(selectedDevices);

    // empty selected devices
    this.setState({ selectedDevices: [] });
  };

  render() {
    const { strings } = this.context;
    const deviceGroups = this.getDeviceGroups();
    const { selectedDevices } = this.state;

    return (
      <div className="devices-action-page">
        <div className="header">
          <p>{strings.removeDevicesFromAssignment}</p>
          <i
            className="fa fa-times icon"
            onClick={() => this.props.changeTab('DEVICES')}
            onKeyDown={() => this.props.changeTab('DEVICES')}
          />
        </div>

        <div className="body">
          {Object.keys(deviceGroups).length === 0 && (
            <DeviceEmpty warningText="No device to be added to assignment" />
          )}

          <div className="assignment-device-selector">
            <DeviceGroupSelectorView
              deviceGroups={deviceGroups}
              handleSelectDevice={this.onSelectDevice}
              handleSelectAllDevices={this.onSelectMultipleDevices}
              selectedDevices={selectedDevices}
            />
          </div>
        </div>

        <div className="footer">
          <div className="footer-inner">
            <div>
              <p>
                {selectedDevices.length} {strings.selected}
              </p>
            </div>

            <button
              type="button"
              onClick={this.removeFromAssignment}
              className={`btnOutline ${selectedDevices.length === 0 ? 'disabled' : ''}`}
            >
              {strings.removeFromAssignment}
            </button>
          </div>
        </div>
      </div>
    );
  }
}

EditDevices.contextType = AssignmentsContext;
EditDevices.propTypes = {
  changeTab: PropTypes.func.isRequired,
};
