import React,{ useEffect, useState } from "react";
import "./Simulacija.css";

function Simulacija({ networkInstance, graphData }) {
  const [simulationSteps, setSimulationSteps] = useState(null);
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [maxFlow, setMaxFlow] = useState(null);

  const updateGraphWithStep = (korak) => {
    if (!networkInstance || !korak || !korak.stanjaBridova) return;

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

    const newEdges = korak.stanjaBridova.map((b) => {
      const newLabel = `${b.tok}/${b.kapacitet}`;

      let color = "#848484";
      if (b.kapacitet > 0) {
        if(b.tok === b.kapacitet) {
          color = "red"; //zasićeni bridovi
        }else if(b.tok < 0) {
          color = "blue";
        } else {
          color = "848484";
        }
      }

      return {
        from: b.pocetniVrh,
        to: b.krajnjiVrh,
        label: newLabel,
        font: { align: "top", size: 20, color: "#000000" },
        color: { color, highlight: color, hover: color, opacity: 1.0 },
        arrows: "to",
      };
    });
    networkInstance.setData({ nodes: updatedNodes,
                           edges: newEdges, 
                          });
  };

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

    if (nextIndex < simulationSteps.length) {
      setCurrentStepIndex(nextIndex);
      updateGraphWithStep(simulationSteps[nextIndex]);
    } else if(nextIndex === simulationSteps.length) {
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

  useEffect(() => {
    handleSimulation();
  }, []);

  return (
    <div className="simulacija-container">
      {simulationSteps && (
        <div>
          <p>
            Korak {currentStepIndex + 1} od {simulationSteps.length} –{" "}
             <strong>
              {simulationSteps[currentStepIndex].akcija}:{" "}aktivan{" "} vrh:{" "}
              {simulationSteps[currentStepIndex].aktivanVrh}
              </strong>
          </p>
          <button className="simulation-button" onClick={handleNextStep}>
            Sljedeći korak
          </button>
          {currentStepIndex === simulationSteps.length - 1 && (
            <div>
              <h3>Simulacija završena!</h3>
              {maxFlow !== null && <p>Maksimalni tok: {maxFlow}</p>}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
  
  export default Simulacija;