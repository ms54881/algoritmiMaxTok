import React,{ useEffect, useState } from "react";
import "./Simulacija.css";

function Simulacija({ networkInstance, graphData }) {
  const [simulationSteps, setSimulationSteps] = useState(null);
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [maxFlow, setMaxFlow] = useState(null);
    const [simulacijaZavrsena, setSimulacijaZavrsena] = useState(false);

  const updateGraphWithStep = (korak) => {
    if (!networkInstance || !korak || !korak.stanjaBridova) return;

    const currentNodes = networkInstance.body.data.nodes.get();
const updatedNodes = currentNodes.map((node) => {
  const { id, ...rest } = node;
  const numericId = parseInt(id, 10);
  const position = networkInstance.getPosition(id);
  const stanjeVrh = korak.stanjaVrhova?.[numericId]; // pristup visini i višku
  const visina = stanjeVrh?.visina ?? "-";
  const visak = stanjeVrh?.visakToka ?? "-";

    const isAktivanVrh = numericId === korak.aktivanVrh;
    const bojaVrh =
      korak.akcija === "promijeniVisinu" && isAktivanVrh
        ? { background: "#ffaaaa", border: "#333" } // Crveni vrh kod relabel
        : undefined;

    return {
      id,
      label: `${id} (h=${visina}, e=${visak})`,
      x: position.x,
      y: position.y,
      color: bojaVrh,
      ...rest,
    };
  });
    const aktivniVrh = korak.aktivanVrh;
  const aktivniBridovi = korak.stanjaBridova.filter(
    (b) =>
      korak.akcija === "guraj" &&
      b.pocetniVrh === aktivniVrh &&
      b.tok > 0
  );

  const newEdges = korak.stanjaBridova
    .filter((b) => b.kapacitet > 0 || b.tok < 0)
    .map((b) => {
      const newLabel = `${b.tok}/${b.kapacitet}`;

      let color = "#848484"; // default: sivo

      const isAktivanBrid = aktivniBridovi.some(
        (ab) => ab.pocetniVrh === b.pocetniVrh && ab.krajnjiVrh === b.krajnjiVrh
      );

      if (isAktivanBrid) {
        color = "#4fa3ff"; // plavi brid za push
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

  networkInstance.setData({
    nodes: updatedNodes,
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

  useEffect(() => {
    handleSimulation();
  }, []);

return (
  <div className="simulacija-container">
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