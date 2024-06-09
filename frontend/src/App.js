
import './App.css';

import axios from 'axios';
import Vendor from './component/Vendor';
import AppContext from './const/AppContext';

function App() {


  const APP_CONTEXT_VALUES = {
    appName: "Payment Processing"
    ,AXIOS_CLIENT: axios.create({
      baseURL: "http://127.0.0.1:8080/"
    })
  }


  return (
    <AppContext.Provider value={APP_CONTEXT_VALUES}>
      <div className="App">
        <Vendor />
      </div>
    </AppContext.Provider>
  );
}

export default App;
