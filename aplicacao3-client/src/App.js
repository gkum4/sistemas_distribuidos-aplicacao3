import './App.css';
import { useCallback, useState } from 'react';
import axios from 'axios';

const App = () => {
  const [clientId, setClientId] = useState('');
  const [resource1Message, setResource1Message] = useState('');
  const [registeredInterestInResource1, setRegisteredInterestInResource1] = useState(false);
  const [registeredInterestInResource2, setRegisteredInterestInResource2] = useState(false);
  const [resource2Message, setResource2Message] = useState('');

  const handleRegisterToResource1 = useCallback(() => {
    if (registeredInterestInResource1) {
      return;
    }

    const eventSource = new EventSource("http://localhost:8080/resource1/" + clientId);
    setRegisteredInterestInResource1(true);

    eventSource.onmessage = (e) => setResource1Message(e.data);

    eventSource.onerror = () => {
      setRegisteredInterestInResource1(false);
      eventSource.close()
    };
  }, [registeredInterestInResource1, clientId]);

  const handleDeregisterResource1 = useCallback(async () => {
    if (!registeredInterestInResource1) {
      return;
    }

    await axios.post("http://localhost:8080/resource1/deregister/" + clientId);

    setRegisteredInterestInResource1(false);
  }, [registeredInterestInResource1, clientId]);

  const handleRegisterToResource2 = useCallback(() => {
    if (registeredInterestInResource2) {
      return;
    }
    
    const eventSource = new EventSource("http://localhost:8080/resource2/" + clientId);
    setRegisteredInterestInResource2(true);

    eventSource.onmessage = (e) => setResource2Message(e.data);

    eventSource.onerror = () => {
      setRegisteredInterestInResource2(false);
      eventSource.close()
    };
  }, [registeredInterestInResource2, clientId]);

  const handleDeregisterResource2 = useCallback(async () => {
    if (!registeredInterestInResource2) {
      return;
    }

    await axios.post("http://localhost:8080/resource2/deregister/" + clientId);

    setRegisteredInterestInResource2(false);
  }, [registeredInterestInResource2, clientId]);

  return (
    <div className="App">
      <h1>Aplicação 3 (Cliente)</h1>
      
      <input placeholder='Id' value={clientId} onChange={e => setClientId(e.target.value)}/>

      <br/>
      <br/>

      <button onClick={handleRegisterToResource1} disabled={registeredInterestInResource1}>Pedir acesso ao RECURSO1</button>
      <button onClick={handleDeregisterResource1} disabled={!registeredInterestInResource1}>Liberar o RECURSO1</button>
      <p>
        Mensagens relacionadas ao RECURSO1:<br/>
        {resource1Message}
      </p>

      <br/>
      <br/>

      <button onClick={handleRegisterToResource2} disabled={registeredInterestInResource2}>Pedir acesso ao RECURSO2</button>
      <button onClick={handleDeregisterResource2} disabled={!registeredInterestInResource2}>Liberar o RECURSO2</button>
      <p>
        Mensagens relacionadas ao RECURSO2:<br/>
        {resource2Message}
      </p>
    </div>
  );
}

export default App;
