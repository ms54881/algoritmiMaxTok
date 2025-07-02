import React,{ useEffect, useState } from "react";
import "./Simulacija.css";

function Simulacija({ networkInstance, graphData }) {
  const [simulationSteps, setSimulationSteps] = useState(null);
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [maxFlow, setMaxFlow] = useState(null);
    const [simulacijaZavrsena, setSimulacijaZavrsena] = useState(false);

const updateGraphWithStep = (korak) => {
  if (!networkInstance || !korak || !korak.stanjaBridova) return;

  const existingNodes = networkInstance.body.data.nodes.get();
  const labelNodesToRemove = existingNodes.filter((n) => typeof n.id === "string" && n.id.startsWith("label_"));
  networkInstance.body.data.nodes.remove(labelNodesToRemove.map((n) => n.id));

  const virtualLabelNodes = [];

  const updatedNodes = existingNodes.map((node) => {
    const { id, ...rest } = node;
    const numericId = parseInt(id, 10);
    if (isNaN(numericId)) return node; 

    const position = networkInstance.getPosition(id);
    const stanjeVrh = korak.stanjaVrhova?.[numericId];
    const visina = stanjeVrh?.visina ?? "-";
    const visak = stanjeVrh?.visakToka ?? "-";

    const isAktivanVrh = numericId === korak.aktivanVrh;
const bojaVrh = isAktivanVrh
  ? korak.akcija === "promijeniVisinu"
    ? { background: "#ffaaaa", border: "#333" }
    : { background: "#ffd9b3", border: "#333" }
  : { background: "#FFFFFF", border: "#848484" }; 


    virtualLabelNodes.push({
      id: `label_${id}`,
      label: `h=${visina}, e=${visak}`,
      shape: "text",
      physics: false,
      x: position.x + 25,
      y: position.y - 40,
      font: { size: 16, color: "#333" },
    });

    return {
      id,
      label: `${id}`,
      x: position.x,
      y: position.y,
      ...rest,
      color: bojaVrh,
    };
  });

  const aktivniVrh = korak.aktivanVrh;
const aktivanBridPocetni = korak.aktivanBridPocetni;
const aktivanBridKrajnji = korak.aktivanBridKrajnji;

const newEdges = korak.stanjaBridova
  .filter((b) => b.kapacitet > 0 || b.tok < 0)
  .map((b) => {
    const isPovratni = !graphData.edges.some(
  (e) =>
    parseInt(e.from, 10) === b.pocetniVrh &&
    parseInt(e.to, 10) === b.krajnjiVrh
);

const newLabel = `${Math.max(0, b.tok)}/${Math.abs(b.kapacitet)}`; 
let color = isPovratni ? "#d3d3d3" : "#848484";
let fontColor = isPovratni ? "#d3d3d3" : "#000000";

if (isPovratni) {
  color = "#d3d3d3"; 
  fontColor = "#d3d3d3"; 
} else {
  fontColor = "#000000"; 
}

    const isAktivanBrid =
      korak.akcija === "guraj" &&
      b.pocetniVrh === aktivanBridPocetni &&
      b.krajnjiVrh === aktivanBridKrajnji;

    if (isAktivanBrid) {
      color = "#4fa3ff";
    }

    return {
      id: `${b.pocetniVrh}-${b.krajnjiVrh}`,
      from: b.pocetniVrh,
      to: b.krajnjiVrh,
      label: newLabel,
      font: { align: "top", size: 20, color: fontColor},
      color: { color, highlight: color, hover: color, opacity: 1.0 },
      arrows: "to",
    };
  });

  networkInstance.body.data.nodes.update([...updatedNodes, ...virtualLabelNodes]);
  networkInstance.body.data.edges.update(newEdges);
};


  const handleSimulation = async () => {
    try {
      if (!networkInstance) return;

          const allEdges = networkInstance.body.data.edges.get();
    const clearedEdges = allEdges.map(edge => ({
      id: edge.id,
      label: "", 
    }));
    networkInstance.body.data.edges.update(clearedEdges);

      const brojVrhova = graphData.nodes.length;
      const bridovi = graphData.edges.map((e) => ({
        pocetniVrh: parseInt(e.from, 10),
        krajnjiVrh: parseInt(e.to, 10),
        kapacitet: parseInt(e.label, 10),
      }));

      const body = {
        brojVrhova,
        bridovi,
        izvor: 0,
        ponor: brojVrhova - 1,
      };

      const response = await fetch("/api/push-relabel/simulacija", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });

      const data = await response.json();

      setSimulationSteps(data.koraci);
      setMaxFlow(data.maksimalniTok || null);
      setCurrentStepIndex(0);

      if (data.koraci && data.koraci.length > 0) {
        updateGraphWithStep(data.koraci[0]);
      }
    } catch (error) {
      console.error("Greška kod simulacije:", error);
    }
  };

  const handleNextStep = () => {
    if (!simulationSteps) return;
    const nextIndex = currentStepIndex + 1;

    if (nextIndex >= simulationSteps.length) {
      setSimulacijaZavrsena(true);
      return;
    } 
    setCurrentStepIndex(nextIndex);
    updateGraphWithStep(simulationSteps[nextIndex]);

    if (nextIndex === simulationSteps.length - 1) {
    setSimulacijaZavrsena(true);
  }
  };

return (
  <div className="simulacija-container">
        {!simulationSteps && (
      <button className="simulation-button" onClick={handleSimulation}>
        Pokreni simulaciju
      </button>
    )}
    {simulationSteps && (
      <div>
        <p className="korak-info">
          Korak {currentStepIndex + 1} od {simulationSteps.length} – {simulationSteps[currentStepIndex].opis}
        </p>

        {!simulacijaZavrsena && (
          <button className="simulation-button" onClick={handleNextStep}>
            Sljedeći korak
          </button>
        )}

        {simulacijaZavrsena && (
          <div className="simulation-end">
            <p className="simulation-finished">Simulacija završena!</p>
            {maxFlow !== null && (
              <p className="max-flow-info">
                <strong>Maksimalni tok: {maxFlow}</strong>
              </p>
            )}
          </div>
        )}
      </div>
    )}
  </div>
);
}
  
  export default Simulacija;