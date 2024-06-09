import {useState} from 'react';
import './VendorItem.css'

export default function VendorItem({vendor, onDeleteClick}) {
    const {id, name, address, vendorId} = vendor;


    return (
        <tr>
          <td>{name}</td>
          <td>{address}</td>
          <td>{vendorId}</td>
          <td>
            <button onClick={e => onDeleteClick(id)}>Delete</button>
          </td>
        </tr>
    );
}