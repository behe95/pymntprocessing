import {useState} from 'react';
import './VendorItem.css'

export default function VendorItem({vendor, onDeleteClick}) {
    const {id, vendorName, vendorAddress} = vendor;


    return (
        <tr>
          <td>{vendorName}</td>
          <td>{vendorAddress}</td>
          <td>
            <button onClick={e => onDeleteClick(id)}>Delete</button>
          </td>
        </tr>
    );
}