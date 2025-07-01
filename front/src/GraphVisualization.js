import React, { useEffect, useRef, useState } from "react";
import { Network } from "vis-network";
import "./GraphVisualization.css";
import Simulacija from "./Simulacija"
import CustomGraphInput from "./CustomGraphInput";

function GraphVisualization() {
  const containerRef = useRef(null);
  const [customGraph, setCustomGraph] = useState(null);
  const [networkInstance, setNetworkInstance] = useState(null);
  const [graphData, setGraphData] = useState(null);
  const [showSimulation, setShowSimulation] = useState(false);

  useEffect(() => {
    if (containerRef.current) {
  containerRef.current.innerHTML = ""; 
    }
    if (!graphData) {
      fetch("/api/push-relabel/primjer")
        .then((res) => res.json())
        .then((data) => {
          const izvor = data.izvor;
          const ponor = data.ponor;
          const nodes = Array.from({ length: data.brojVrhova }, (_, i) => ({
            id: i,
            label: i.toString(),
            color: i === izvor ? "#ffb3b3" : i === ponor ? "#b3ffb3" : "#FFFFFF",
          }));

          const edges = data.bridovi.map((b) => ({
            id: `${b.pocetniVrh}-${b.krajnjiVrh}`,
            from: b.pocetniVrh,
            to: b.krajnjiVrh,
            label: b.kapacitet.toString(),
            font: { align: "top" },
          }));

          setGraphData({ nodes, edges, izvor, ponor });
        })
        .catch((err) => console.error("Greška pri dohvatu grafa:", err));
    }
  }, [graphData]);

  useEffect(() => {
    if (!graphData) return;

    const { nodes, edges, izvor, ponor } = graphData;
    const updatedNodes = nodes.map((node) => ({
      ...node,
      color: node.id === izvor ? "#ffb3b3" : node.id === ponor ? "#b3ffb3" : "#FFFFFF", 
    }));
    const options = {
      physics: {
        enabled: true,
        stabilization: {
          iterations: 100,
        },
      },
      interaction: {
        hover: true,
        dragNodes: true,
      },
      layout: {
        improvedLayout: false,
      },
      edges: {
        smooth: {
          type: "horizontal",
        },
        arrows: "to",
        font: {
          size: 16,
          color: "#343434",
        },
        color: {
          color: "#848484",
          highlight: "#848484",
          hover: "#848484",
        },
      },
      nodes: {
        shape: "circle",
        color: {
          background: "#FFFFFF",
          border: "#848484",
          highlight: {
            background: "#D2E5FF",
            border: "#2B7CE9",
          },
        },
        font: {
          size: 16,
          color: "#000000",
        },
      },
    };

    const network = new Network(containerRef.current, { nodes: updatedNodes, edges }, options);
    network.on("stabilizationIterationsDone", () => {
      console.log("Fizikalna stabilizacija dovršena.");
      network.setOptions({ physics: { enabled: false } });
    });

    setNetworkInstance(network);
  }, [graphData]);

  const handleLockPositions = () => {
    if (!networkInstance) return;

    const allNodeIds = networkInstance.body.nodeIndices;
    allNodeIds.forEach((nodeId) => {
      networkInstance.body.nodes[nodeId].options.fixed = {
        x: true,
        y: true,
      };
    });

    networkInstance.setOptions({
      physics: {
        enabled: false,
      },
      interaction: {
        hover: true,
        dragNodes: false,
      },
    });
    console.log("Čvorovi zaključani!");
  };

  const handleCustomGraphSubmit = (customGraphData) => {
    const nodes = Array.from({ length: customGraphData.brojVrhova }, (_, index) => ({
      id: index,
      label: index.toString(),
      color: index === customGraphData.izvor ? "#ffb3b3" : index === customGraphData.ponor ? "#b3ffb3" : "#FFFFFF",
    }));

    const edges = customGraphData.bridovi.map((brid) => ({
      id: `${brid.pocetniVrh}-${brid.krajnjiVrh}`,
      from: parseInt(brid.pocetniVrh),
      to: parseInt(brid.krajnjiVrh),
      label: brid.kapacitet.toString(),
      font: { align: "top" },
    }));

    setGraphData({ nodes, edges, izvor: customGraphData.izvor,
      ponor: customGraphData.ponor
     });
setGraphData({ nodes, edges, izvor: customGraphData.izvor, ponor: customGraphData.ponor });
setCustomGraph(false);
setShowSimulation(false); // simulacija će se uključiti automatski kad sve bude spremno

  };

  useEffect(() => {
  if (graphData && networkInstance) {
    setShowSimulation(true);
  }
}, [graphData, networkInstance]);


  return (
    <div className="main-container">
      <div className="decorative-lines"></div>
      <h1 className="graph-title">Push-Relabel algoritam</h1>
      <div className="button-group">
        <button
          className="create-graph-button"
          onClick={() => setCustomGraph(true)}
        >
          Kreiraj vlastiti graf
        </button>

        <button className="lock-button" onClick={handleLockPositions}>
          Zaključaj čvorove
        </button>
        {!showSimulation && (
          <button
            className="simulation-button"
            onClick={() => setShowSimulation(true)}
          >
            Pokreni simulaciju
          </button>
        )}
      </div>

      {customGraph && (
        <CustomGraphInput
          onClose={() => setCustomGraph(false)}
          onSubmit={handleCustomGraphSubmit}
        />
      )}
      {showSimulation && (
        <Simulacija 
          networkInstance={networkInstance} 
          graphData={graphData}
          izvor={graphData?.izvor}
          ponor={graphData?.ponor}
         />
      )}

      <div className="graph-container">
        <div ref={containerRef} className="graph" />
      </div>
    </div>
  );
}

export default GraphVisualization;