import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import "./App.css";
import GraphVisualization from "./GraphVisualization";
import EdmondKarp from "./EdmondKarp";
import Dinic from "./Dinic";

function WelcomePage() {
  return (
    <div className="welcome-container">
    <div className="decorative-lines"></div>
    <div className="welcome-content">
      <h1 className="welcome-title">Dobrodošli</h1>
      <p className="welcome-subtitle">
        Ovo je aplikacija u kojoj možete isprobati algoritme za rješavanje
        problema maksimalnog toka u mreži. Odaberite algoritam koji želite
        isprobati!
      </p>

        <a href="/api/push-relabel" className="algo-button">
          Push-Relabel algoritam
        </a>
        <a href="/api/edmondkarp" className="algo-button">
          Edmonds-Karp algoritam
        </a>
        <a href="/api/dinic" className="algo-button">
          Dinicov algoritam
        </a>
      </div>
    </div>
  );
}

function App() {
  return (
    <Router>
      <div className="decorative-lines"></div>
      <Routes>
        <Route path="/" element={<WelcomePage />} />
        <Route path="/api/push-relabel" element={<GraphVisualization />} />
        <Route path="/api/edmondkarp" element={<EdmondKarp />} />
        <Route path="/api/dinic" element={<Dinic />} />
      </Routes>
    </Router>
  );
}

export default App;


