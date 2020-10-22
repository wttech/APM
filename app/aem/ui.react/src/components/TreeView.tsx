import React, {useEffect, useState, useRef, MutableRefObject} from 'react';
import axios from "axios";
import TreeNodeComponent from './TreeNodeComponent';
import {Network, DataSet, Edge, Node, Data} from "vis-network/standalone";
import {forEach} from "vis-util";

export type Script = {
    name: string,
    path: string,
    valid: boolean
};

export type TreeNode = {
    script: Script,
    isValid: boolean
};

export type Transition = {
    from: TreeNode,
    to: TreeNode,
    cycleDetected: boolean,
    refType: string
}

type Graph = {
    nodes: TreeNode[],
    transitions: Transition[]
}

export const fetchData = async (): Promise<Graph | null> => {
    const data = await axios.get('/bin/apm/graph');
    if (data.status === 200) {
        return data.data;
    }
    return null;
};

export const TreeView = () => {
    const [graphData, setData] = useState<Graph | null>(null);
    const [nodes, setNodes] = useState<TreeNode[] | undefined>([])
    const [transitions, setTransitions] = useState<Transition[] | undefined>([]);


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
                setNodes(data?.nodes)
                setTransitions(data?.transitions);
            })
            .then(() =>
                console.log(nodes));

    }, []);

    useEffect(() => {
        console.log("Second use effect" + nodes);
        if (nodes && transitions && null !== container.current) {
            const networkNodes = new DataSet<Node, "id">();
            nodes?.forEach((node) => {
                networkNodes.add({id: node.script.name, label: node.script.name, shape: 'box'});
            })

            const edges = new DataSet<Edge, "id">();
            transitions?.forEach((transition) => {
                edges.add({
                    from: transition.from.script.name,
                    to: transition.to.script.name,
                    id: `${transition.refType}${transition.from.script.name}${transition.to.script.name}`,
                    title: transition.cycleDetected ? 'INVALID' : transition.refType,
                    smooth: {
                        enabled: true,
                        type: 'curvedCW',
                        roundness: Math.random()
                    },
                    color: transition.cycleDetected ? 'red' : (transition.refType === 'IMPORT' ? 'blue' : 'green')
                });
            })


            const data: Data = {
                nodes: networkNodes,
                edges: edges
            };

            new Network(container.current, data, options);
        }
    }, [nodes, transitions])

    /*    useEffect(() => {
            if (null !== container.current) {
                console.log(container);
                const network: Network = new Network(container.current, data, options);
                console.log(network);
            }
        }, [container])*/


    const markup = nodes && nodes.map((item, key) =>
        <div>{item.script.name}</div>
    );/* items && items.map((item, key) =>
      <TreeNodeComponent key={key} script={item.script} children={item.children}/>);
*/
    return <div ref={container} id="graph"/>
};