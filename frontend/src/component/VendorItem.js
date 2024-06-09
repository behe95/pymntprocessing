import {useState} from 'react';
import './VendorItem.css'
import CONSTANTS from '../const/CONSTANTS';

export default function VendorItem({vendor, onDeleteClick, updateVendor}) {
    const {id} = vendor;
    const [isEditMode, setIsEditMode] = useState(false);


    const [vendorName, setVendorName] = useState(vendor.name);
    const [vendorAddress, setVendorAddress] = useState(vendor.address);
    const [vendorId, setVendorId] = useState(vendor.vendorId);

    const editModeHandler = () => {
      setIsEditMode(true);
    }

    const onUpdateClick = () => {
      setIsEditMode(false);
      const vendor = {
        id
        ,name: vendorName
        ,address: vendorAddress
        ,vendorId: vendorId
      };
      updateVendor(vendor);
    }
    
    /**
     * Function to track input change
     */
    const onChangeHandler = (e, inputType) => {
      switch (inputType) {
          case CONSTANTS.VENDOR_INPUT_FIELD_TYPE.vendorNameInput:
              setVendorName(e.target.value);            
              break;
          case CONSTANTS.VENDOR_INPUT_FIELD_TYPE.vendorAddresInput:
              setVendorAddress(e.target.value);            
              break;
          case CONSTANTS.VENDOR_INPUT_FIELD_TYPE.vendorIdInput:
              setVendorId(e.target.value);            
              break;    
          default:
              break;
      }
    }

    return (
        <tr onDoubleClick={() => editModeHandler()}>
          <td>            
            {
                isEditMode ?
                (
                  <input value={vendorName} onChange={(e) => onChangeHandler(e, CONSTANTS.VENDOR_INPUT_FIELD_TYPE.vendorNameInput)} type='text' autoFocus />
                )
                :
                (
                  vendorName
                )
              }
          </td>
          <td>
            {
              isEditMode ?
              (
                <input value={vendorAddress} onChange={(e) => onChangeHandler(e, CONSTANTS.VENDOR_INPUT_FIELD_TYPE.vendorAddresInput)} type='text' autoFocus />
              )
              :
              (
                vendorAddress
              )
            }
          </td>
          <td>
            {
              isEditMode ?
              (
                <input value={vendorId} onChange={(e) => onChangeHandler(e, CONSTANTS.VENDOR_INPUT_FIELD_TYPE.vendorIdInput)} type='text' autoFocus />
              )
              :
              (
                vendorId
              )
            }

          </td>
          <td>
            <button onClick={e => onDeleteClick(id)}>Delete</button>
            {
              isEditMode && <button onClick={e => onUpdateClick()}>Update</button>
            }
          </td>
        </tr>
    );
}