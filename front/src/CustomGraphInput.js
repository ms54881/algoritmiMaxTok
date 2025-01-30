import React, { useState } from 'react';

function CustomGraphInput({ onClose, onSubmit }) {
  const [brojVrhova, setBrojVrhova] = useState('');
  const [bridovi, setBridovi] = useState([]);

  const addBrid = () => {
    setBridovi([...bridovi, { pocetniVrh: '', krajnjiVrh: '', kapacitet: '' }]);
  };

  const handleBridChange = (index, field, value) => {
    const newBridovi = [...bridovi];
    newBridovi[index][field] = value === "" ? "" : parseInt(value, 10);
    setBridovi(newBridovi);
  };

  const handleSubmit = () => {
    if (
        brojVrhova === "" ||
        isNaN(parseInt(brojVrhova, 10)) ||
        bridovi.some(
          (brid) =>
            brid.pocetniVrh === "" ||
            brid.krajnjiVrh === "" ||
            isNaN(brid.pocetniVrh) ||
            isNaN(brid.krajnjiVrh) ||
            isNaN(brid.kapacitet)
        )
      ) {
        alert("Molimo unesite ispravne podatke za graf.");
        return;
      }

    const customGraph = { brojVrhova: parseInt(brojVrhova, 10), bridovi };
    console.log('Custom graph submitted:', customGraph);
    // Ovdje možete poslati podatke na backend ili izgenerirati graf
    onSubmit(customGraph);
  };

  return (
    <div className="custom-graph-input">
      <h2>Unesi vlastiti graf</h2>
      <div>
        <label>Broj vrhova:</label>
        <input
          type="number"
          value={brojVrhova}
          onChange={(e) => setBrojVrhova(e.target.value)}
        />
      </div>
      {bridovi.map((brid, index) => (
        <div key={index} className="brid-input">
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
