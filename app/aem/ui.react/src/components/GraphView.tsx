import React, {useEffect, useState, useRef, MutableRefObject} from 'react';
import axios from "axios";
import {Network, DataSet, Edge, Node, Data} from "vis-network/standalone";

export type Script = {
    name: string,
    path: string,
    valid: boolean
};

export type GraphNode = {
    script: Script,
    isValid: boolean
};

export type Transition = {
    from: GraphNode,
    to: GraphNode,
    cycleDetected: boolean,
    refType: string
}

type Graph = {
    nodes: GraphNode[],
    transitions: Transition[]
}

export const fetchData = async (): Promise<Graph | null> => {
    const data = await axios.get('/bin/apm/graph');
    if (data.status === 200) {
        return data.data;
    }
    return null;
};

export const GraphView = () => {
    const [graphData, setData] = useState<Graph | null>(null);

    const options = {
        physics: false,
        edges: {arrows: 'to'},
        layout: {hierarchical: { nodeSpacing: 300, sortMethod: 'directed'}},
        interaction: {dragNodes: false, dragView: false, zoomView: false}
    };

    const container: MutableRefObject<HTMLDivElement | null> = useRef<HTMLDivElement | null>(null);
    useEffect(() => {
        fetchData()
            .then((data) => {
                console.log(data);
                setData(data);
            })
            .then(() =>
                console.log(graphData?.nodes));

    }, []);

    useEffect(() => {
        const nodes = graphData?.nodes;
        const transitions = graphData?.transitions;
        if (nodes && transitions && null !== container.current) {
            const networkNodes = new DataSet<Node, 'id'>();
            nodes?.forEach((node) => {
                networkNodes.add({
                    id: node.script.name,
                    label: node.script.name,
                    shape: 'box',
                    size: 30,
                    margin: {
                        top: 10,
                        bottom: 10,
                        right: 10,
                        left: 10
                    },
                    borderWidth: 2,
                    color: {
                        background: '#bbd7f0',
                        border: '#448CCB',
                    },
                    font: {
                        size: 16,
                        color: '#448CCB',
                    },
                    title: node.script.path
                });
            })

            const edges = new DataSet<Edge, 'id'>();
            transitions?.forEach((transition) => {
                edges.add({
                    from: transition.from.script.name,
                    to: transition.to.script.name,
                    title: transition.cycleDetected ? 'INVALID' : transition.refType,
                    smooth: {
                        enabled: true,
                        type: 'curvedCW',
                        roundness: Math.random()
                    },
                    width: 2,
                    shadow: {
                        enabled: true,
                        size: 5,
                        color: 'rgba(0,0,0,0.2)'
                    },
                    color: transition.cycleDetected ? 'red' : (transition.refType === 'IMPORT' ? '#0b3f6e' : '#69c44d')
                });
            })

            const data: Data = {
                nodes: networkNodes,
                edges: edges
            };

            new Network(container.current, data, options);
        }
    }, [graphData])

    return <div ref={container} id="graph"/>
};