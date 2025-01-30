import React, { useState } from 'react';

function CustomGraphInput({ onClose, onSubmit }) {
  const [brojVrhova, setBrojVrhova] = useState('');
  const [bridovi, setBridovi] = useState([]);
  const [izvor, setIzvor] = useState('');
  const [ponor, setPonor] = useState('');

  const addBrid = () => {
    setBridovi([...bridovi, { pocetniVrh: '', krajnjiVrh: '', kapacitet: '' }]);
  };

  const handleBridChange = (index, field, value) => {
    const newBridovi = [...bridovi];
    newBridovi[index][field] = value === "" ? "" : parseInt(value, 10);
    setBridovi(newBridovi);
  };

  const isGraphConnected = (bridovi, brojVrhova, source, sink) => {
    const adjacencyList = Array.from({ length: brojVrhova }, () => []);
  
    bridovi.forEach(({ pocetniVrh, krajnjiVrh }) => {
      adjacencyList[pocetniVrh].push(krajnjiVrh);
    });
  
    const visited = new Array(brojVrhova).fill(false);
    const queue = [source];
    visited[source] = true;
  
    while (queue.length > 0) {
      const node = queue.shift();
      for (const neighbor of adjacencyList[node]) {
        if (!visited[neighbor]) {
          visited[neighbor] = true;
          queue.push(neighbor);
        }
      }
    }
  
    return visited[sink];
  };

  const hasDuplicateEdges = (bridovi) => {
    const edgeSet = new Set();
  
    for (const { pocetniVrh, krajnjiVrh } of bridovi) {
      const edge = `${pocetniVrh}-${krajnjiVrh}`;
      if (edgeSet.has(edge)) {
        return true;
      }
      edgeSet.add(edge);
    }
  
    return false;
  };

  const handleSubmit = () => {
    const brojVrhovaInt = parseInt(brojVrhova, 10);
    const izvorInt = parseInt(izvor, 10);
    const ponorInt = parseInt(ponor, 10);
    if (isNaN(brojVrhovaInt) || brojVrhovaInt < 2) {
      alert("Broj vrhova mora biti broj veći ili jednak 2.");
      return;
    }

    if (
      isNaN(izvorInt) ||
      isNaN(ponorInt) ||
      izvorInt < 0 ||
      ponorInt < 0 ||
      izvorInt >= brojVrhovaInt ||
      ponorInt >= brojVrhovaInt
    ) {
      alert("Izvor i ponor moraju biti valjani čvorovi unutar grafa.");
      return;
    }

    if (
        bridovi.some(
          (brid) =>
          isNaN(brid.pocetniVrh) ||
          isNaN(brid.krajnjiVrh) ||
          isNaN(brid.kapacitet) ||
          brid.pocetniVrh < 0 ||
          brid.krajnjiVrh < 0 ||
          brid.kapacitet <= 0 ||
          brid.pocetniVrh >= brojVrhovaInt ||
          brid.krajnjiVrh >= brojVrhovaInt
        )
      ) {
        alert("Molimo unesite ispravne podatke za graf.");
        return;
      }

      if (!isGraphConnected(bridovi, brojVrhovaInt, izvorInt, ponorInt)) {
        alert("Graf nije povezan! Mora postojati put od izvora do ponora.");
        return;
      }

      if (hasDuplicateEdges(bridovi)) {
        alert("Graf sadrži duplikate bridova! Provjerite svoje unose.");
        return;
      }

    const customGraph = { brojVrhova: brojVrhovaInt,
      izvor: izvorInt,
      ponor: ponorInt,
      bridovi };
    console.log('Custom graph submitted:', customGraph);
    onSubmit(customGraph);
  };

  return (
    <div className="custom-graph-input">
      <h2>Unesi vlastiti graf</h2>
      <small style={{ fontSize: "12px", color: "#666" }}>(Čvorovi počinju od 0)</small>
      <div>
        <label>Broj vrhova:</label>
        <input
          type="number"
          value={brojVrhova}
          onChange={(e) => setBrojVrhova(e.target.value)}
        />
      </div>
      <div>
        <label>Izvor (početni čvor):</label>
        <input
          type="number"
          value={izvor}
          onChange={(e) => setIzvor(e.target.value)}
        />
      </div>
      <div>
        <label>Ponor (krajnji čvor):</label>
        <input
          type="number"
          value={ponor}
          onChange={(e) => setPonor(e.target.value)}
        />
      </div>
      {bridovi.map((brid, index) => (
        <div key={index} className="brid-inputs">
          <input
            type="number"
            placeholder="Početni vrh"
            value={brid.pocetniVrh === "" ? "" : brid.pocetniVrh}
            onChange={(e) => handleBridChange(index, 'pocetniVrh', e.target.value)}
          />
          <input
            type="number"
            placeholder="Krajnji vrh"
            value={brid.krajnjiVrh === "" ? "" : brid.krajnjiVrh}
            onChange={(e) => handleBridChange(index, 'krajnjiVrh', e.target.value)}
          />
          <input
            type="number"
            placeholder="Kapacitet"
            value={brid.kapacitet}
            onChange={(e) => handleBridChange(index, 'kapacitet', e.target.value)}
          />
        </div>
      ))}
      <button onClick={addBrid}>Dodaj brid</button>
      <button onClick={handleSubmit}>Potvrdi</button>
      <button onClick={onClose}>Nazad</button>
    </div>
  );
}

export default CustomGraphInput;
