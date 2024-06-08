import './App.css';
import {useState} from 'react';
import VendorItem from './component/VendorItem';

let id = 0;

function App() {

  const [vendors, setVendors] = useState([]);

  const [vendorName, setVendorName] = useState("");
  const [vendorAddress, setVendorAddress] = useState("");

  /**
   * Function to save data
   */
  function onSaveClick() {
    const vendor = {
      id: id++
      ,vendorName
      ,vendorAddress
    }

    if(vendorName.length > 0 &&
      vendorAddress.length > 0
    ) {
      // vendors.push(vendor);
      setVendors(vendors => [...vendors, vendor])
      // Reset data
      setVendorName("");
      setVendorAddress("");
    }   

  }

  /**
   * Function to delete data
   */
  function onDeleteClick(id) {
    const idx = vendors.findIndex(vendor => vendor.id == id);
    vendors.splice(idx, idx+1);
    setVendors(vendors => [...vendors])
  }



  return (
    <div className="App">
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
            <div className="grid-column grid-column-right">
              <div className="grid-item">
                <button onClick={() => onSaveClick()}>Save</button>
              </div>
            </div>
          </div>

        </div>

        <div className='grid-container stretch-vertically'>
          <table className='vendor-table'>
            <tr>
              <th>Vendor Name</th>
              <th>Vendor Address</th>
              <th></th>          
            </tr>
            {
              vendors.map(vendor => (
                <VendorItem 
                  key={vendor.id} 
                  vendor={vendor} 
                  onDeleteClick={onDeleteClick} />
              ))
            }
          </table>
        </div>

      </div>
    </div>
  );
}

export default App;
