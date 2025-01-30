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
    if (!graphData) {
      fetch("/api/push-relabel/primjer")
        .then((res) => res.json())
        .then((data) => {
          const nodes = Array.from({ length: data.brojVrhova }, (_, i) => ({
            id: i,
            label: i.toString(),
          }));

          const edges = data.bridovi.map((b) => ({
            from: b.pocetniVrh,
            to: b.krajnjiVrh,
            label: b.kapacitet.toString(),
            font: { align: "top" },
          }));

          setGraphData({ nodes, edges });
        })
        .catch((err) => console.error("Greška pri dohvatu grafa:", err));
    }
  }, [graphData]);

  useEffect(() => {
    if (!graphData) return;

    const { nodes, edges } = graphData;
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

    const network = new Network(containerRef.current, { nodes, edges }, options);
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
    }));

    const edges = customGraphData.bridovi.map((brid) => ({
      from: parseInt(brid.pocetniVrh),
      to: parseInt(brid.krajnjiVrh),
      label: brid.kapacitet.toString(),
      font: { align: "top" },
    }));

    setGraphData({ nodes, edges });
    setCustomGraph(false); // Zatvori formu
  };

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
        <Simulacija networkInstance={networkInstance} graphData={graphData} />
      )}

      <div className="graph-container">
        <div ref={containerRef} className="graph" />
      </div>
    </div>
  );
}

export default GraphVisualization;