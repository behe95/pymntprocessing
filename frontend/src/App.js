import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <div className="App">
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
              <input type="text" className="grid-item-stretch" />
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
              <input type="text" className="grid-item-stretch" />
            </div>
          </div>

        </div>

        <div className="grid-row">
          <div className="grid-column grid-column-right">
            <div className="grid-item">
              <button>Save</button>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}

export default App;
