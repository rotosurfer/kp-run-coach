import React from 'react';
import './App.css';
import RegisterForm from './components/auth/RegisterForm';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>KP Run Coach</h1>
        <p>Training Management Platform</p>
      </header>
      <main>
        <div className="auth-container">
          <h2>Coach Registration</h2>
          <RegisterForm />
        </div>
      </main>
    </div>
  );
}

export default App;
