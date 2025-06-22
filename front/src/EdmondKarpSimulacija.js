import React, {useEffect, useState } from "react";
import "./Simulacija.css";

function EdmondKarpSimulacija({ networkInstance, graphData }) {
  const [simulationSteps, setSimulationSteps] = useState(null);
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [maxFlow, setMaxFlow] = useState(null);
  const pathColors = ["#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b"];
  const pathColorIndex = React.useRef(0);  // indeks trenutno korištene boje
  const previousColoredEdges = React.useRef([]);

const updateGraphWithStep = (korak) => {
  if (!networkInstance || !korak || !korak.stanjaBridova) return;

  const currentNodes = networkInstance.body.data.nodes.get();
  const updatedNodes = currentNodes.map((node) => {
    const { id, label, ...rest } = node;
    const position = networkInstance.getPosition(id);
    return {
      id,
      label,
      x: position.x,
      y: position.y,
      ...rest,
    };
  });

  // Defaultno svi bridovi sivi
  const allEdges = korak.stanjaBridova
    .filter((b) => b.tok >= 0)
    .map((b) => {
      const newLabel = `${b.tok}/${b.kapacitet}`;
      return {
        from: b.pocetniVrh,
        to: b.krajnjiVrh,
        label: newLabel,
        font: { align: "top", size: 20, color: "#000000" },
        color: { color: "#848484", highlight: "#848484", hover: "#848484" },
        arrows: "to",
      };
    });

  // Ako postoji put u ovom koraku – oboji ga posebnom bojom
  const aktivniPut = korak.put || [];
  const boja = pathColors[pathColorIndex.current % pathColors.length];

  const aktivniBridovi = aktivniPut.map(([from, to]) => ({
    from,
    to,
    label: `${findTokLabel(korak.stanjaBridova, from, to)}`,
    font: { align: "top", size: 20, color: "#000000" },
    color: { color: boja, highlight: boja, hover: boja },
    width: 3,
    arrows: "to",
  }));

  previousColoredEdges.current = [...previousColoredEdges.current, ...aktivniBridovi];

const allUpdatedEdges = [...allEdges]; // svi bridovi iz koraka

// Dodaj aktivne bridove, ali ukloni duplikate po (from, to)
const aktivneIds = aktivniBridovi.map(b => `${b.from}-${b.to}`);
const stareBoje = previousColoredEdges.current.filter(
  b => !aktivneIds.includes(`${b.from}-${b.to}`)
);

// Ažuriraj memoriju i stanje grafa
previousColoredEdges.current = [...stareBoje, ...aktivniBridovi];

networkInstance.setData({
  nodes: updatedNodes,
  edges: [...allUpdatedEdges, ...previousColoredEdges.current],
});
};

function findTokLabel(bridovi, from, to) {
  const match = bridovi.find(
    (b) => b.pocetniVrh === from && b.krajnjiVrh === to
  );
  return match ? `${match.tok}/${match.kapacitet}` : "";
}


  const handleSimulation = async () => {
    try {
      if (!networkInstance) return;

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

      const response = await fetch("/api/edmondkarp/simulacija", {
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
        pathColorIndex.current++;
      }
    } catch (error) {
      console.error("Greška kod simulacije:", error);
    }
  };

  const handleNextStep = () => {
    if (!simulationSteps) return;
    const nextIndex = currentStepIndex + 1;
    if (nextIndex < simulationSteps.length) {
      setCurrentStepIndex(nextIndex);
      updateGraphWithStep(simulationSteps[nextIndex]);
      pathColorIndex.current++;  // Svaki novi korak koristi novu boju
    }else if(nextIndex === simulationSteps.length) {
      const finalStep = simulationSteps[simulationSteps.length - 1];
    const finalEdges = finalStep.stanjaBridova.map((b) => ({
      from: b.pocetniVrh,
      to: b.krajnjiVrh,
      label: `${b.tok}/${b.kapacitet}`,
      font: { align: "top", size: 20, color: "#000000" },
      color: b.tok > 0 ? "green" : "#848484", // Zeleni bridovi za konačni tok
      arrows: "to",
    }));

    const currentNodes = networkInstance.body.data.nodes.get();
    const updatedNodes = currentNodes.map((node) => {
      const{ id, label, ...rest} = node;
      const position = networkInstance.getPosition(id);

      return {
        id, 
        label,
        x:position.x,
        y:position.y,
        ...rest,
      };
  });

    networkInstance.setData({
      nodes: updatedNodes,
      edges: finalEdges,
    });
    }
  };
  useEffect(() => {handleSimulation()}, []);

  return (
    <div className="simulacija-container">

      {simulationSteps && (
        <div>
          <p className="korak-info">
          Korak {currentStepIndex + 1} od {simulationSteps.length} – {simulationSteps[currentStepIndex].akcija}
          </p>
          <button className="simulation-button" onClick={handleNextStep}>
            Sljedeći korak
          </button>
          {currentStepIndex === simulationSteps.length - 1 && (
            <div>
              <h3>Simulacija završena!</h3>
              {maxFlow !== null && <p className="max-flow-info"><strong>Maksimalni tok: {maxFlow}</strong></p>}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default EdmondKarpSimulacija;
