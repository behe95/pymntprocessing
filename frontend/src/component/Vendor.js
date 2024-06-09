import '../App.css';
import {useCallback, useEffect, useState} from 'react';
import VendorItem from './VendorItem';

import axios from 'axios';

let id = 0;

const client = axios.create({
  baseURL: "http://127.0.0.1:8080/"
})

function Vendor() {
  const [vendors, setVendors] = useState([]);

  const [vendorName, setVendorName] = useState("");
  const [vendorAddress, setVendorAddress] = useState("");
  const [vendorId, setVendorId] = useState("");
  const [loading, setLoading] = useState(true);

  /**
   * Function to save data
   */
  function onSaveClick() {
    const vendor = {
      id: id++
      ,name: vendorName
      ,address: vendorAddress
      ,vendorId: vendorId
    }

    if(vendorName.length > 0 &&
      vendorAddress.length > 0
    ) {
      setLoading(true);

      setVendors(vendors => [...vendors, vendor])
      // Reset data
      setVendorName("");
      setVendorAddress("");

      // send to the backend
      client.post("/vendor/add", {
        name: vendor.name
        ,address: vendor.address
        ,vendorId: vendor.vendorId
      })
      .then(response => {
        setLoading(false);
        console.log(response);
      })
      .catch(err => {
        setLoading(false);
        console.log(err);
      });

    }       
    
    
    
  }
    
  /**
   * Function to delete data
   */
  function onDeleteClick(id) {

    // delete request to backend
    client.delete("")


    const idx = vendors.findIndex(vendor => vendor.id === id);
    vendors.splice(idx, idx+1);
    setVendors(prev => [...prev])
  }

  // useEffect(() => {
  //   console.log("Mouned " + loading)
  // }, [loading])


  
  /**
   * Render on Mount and Unmount
   */
  useEffect(() => {

    if(loading) {

      // get vendors
      client.get("/vendor/get").then(response => {
        const {data} = response;  
        setVendors(prev => [...prev, ...data]);
        setLoading(false);
        
        vendors.forEach(vendor => {
          id = vendor.id;
        });

      })
      .catch(err => {
        setLoading(false);
        console.log(err);
      });
    }
    // cleanup vendors on unMount
    return () => {  
      setVendors([]);
    };
  },[])


  return (
    <div className="App">
      {
        loading &&
        <div>
         <p>Loading</p>
        </div>
      }
      <div className='page stretch-vertically'>
        <div className="grid-container">

          <div className="grid-row grid-item-stretch">
            
            <div className="grid-column">
              <div className="grid-item grid-item-stretch">
                <label>Add Vendor</label>
              </div>
            </div>
            

          </div>


          <div className="grid-row">
            
            <div className="grid-column">
              <div className="grid-item">
                <label>Name</label>
              </div>
            </div>
            
            <div className="grid-column">
              <div className="grid-item">
                <input type="text" className="grid-item-stretch" value={vendorName} onChange={e => setVendorName(e.target.value)}/>
              </div>
            </div>

          </div>

          <div className="grid-row">
            
            <div className="grid-column">
              <div className="grid-item">
                <label>Address</label>
              </div>
            </div>
            
            <div className="grid-column">
              <div className="grid-item">
                <input type="text" className="grid-item-stretch" value={vendorAddress} onChange={e => setVendorAddress(e.target.value)} />
              </div>
            </div>

          </div>

          <div className="grid-row">
            
            <div className="grid-column">
              <div className="grid-item">
                <label>VendorId</label>
              </div>
            </div>
            
            <div className="grid-column">
              <div className="grid-item">
                <input type="text" className="grid-item-stretch" value={vendorId} onChange={e => setVendorId(e.target.value)} />
              </div>
            </div>

          </div>

          <div className="grid-row">
            <div className="grid-column grid-column-right">
              <div className="grid-item">
                <button onClick={() => onSaveClick()}>Save</button>
              </div>
            </div>
          </div>

        </div>

        <div className='grid-container stretch-vertically'>
          <table className='vendor-table'>
            <thead>              
              <tr>
                <th>Vendor Name</th>
                <th>Vendor Address</th>
                <th></th>          
              </tr>
            </thead>
            <tbody>
              {
                vendors.map((vendor) => (
                  <VendorItem 
                    key={vendor.id} 
                    vendor={vendor} 
                    onDeleteClick={onDeleteClick} />
                ))
              }
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
}

export default Vendor;
