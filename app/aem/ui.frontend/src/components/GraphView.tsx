/*
* ========================LICENSE_START=================================
* AEM Permission Management
* %%
* Copyright (C) 2013 Cognifide Limited
* %%
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* =========================LICENSE_END==================================
*/
import React, {useEffect, useState, useRef, MutableRefObject} from 'react';
import axios from "axios";
import {Network, DataSet, Edge, Node} from "vis-network/standalone";

type Script = {
    name: string,
    path: string,
    valid: boolean
};

type GraphNode = {
    id: string,
    script: Script,
    valid: boolean
};

type Transition = {
    from: GraphNode,
    to: GraphNode,
    cycleDetected: boolean,
    refType: string
}

type Graph = {
    nodes: GraphNode[],
    transitions: Transition[]
}

const fetchData = async (): Promise<Graph | null> => {
    const data = await axios.get('/bin/apm/graph');
    if (data.status === 200) {
        return data.data;
    }
    return null;
};

const theme = {
    apm_blue: '#448CCB',
    blue_bg: '#bbd7f0',
    invalid_fg: '#e80707',
    invalid_bg: '#c77575',
    import_edge: '#3ea638',
    run_edge: '#d8db2c',
    width: 2
};

export const GraphView = () => {
    const [graphData, setData] = useState<Graph | null>(null);
    const container: MutableRefObject<HTMLDivElement | null> = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        fetchData()
            .then((data) => {
                setData(data);
            });
    }, []);

    useEffect(() => {
        const convertToEdges = (transitions: Transition[]): DataSet<Edge, 'id'> => {

            const edges = new DataSet<Edge, 'id'>();

            transitions.forEach((transition) => {
                const existingItem = edges.getDataSet().getIds().find((id) => {
                    return (id.toString().startsWith(`${transition.from.id}${transition.to.id}`) ||
                        id.toString().startsWith(`${transition.to.id}${transition.from.id}`));
                });

                let smoothOpts: boolean | {
                    enabled: boolean,
                    type: string,
                    forceDirection?: string | boolean,
                    roundness: number,
                } = false;


                if (existingItem) {
                    smoothOpts = {
                        enabled: true,
                        type: 'curvedCW',
                        roundness: 0.3
                    }
                }

                edges.add({
                    id: `${transition.from.id}${transition.to.id}|${transition.refType}`,
                    from: transition.from.id,
                    to: transition.to.id,
                    title: transition.cycleDetected ? 'INVALID' : transition.refType,
                    smooth: smoothOpts,
                    width: theme.width,
                    shadow: {
                        enabled: true,
                        size: 5,
                        color: 'rgba(0,0,0,0.2)'
                    },
                    color: transition.cycleDetected ? theme.invalid_fg : (transition.refType === 'IMPORT' ? theme.import_edge : theme.run_edge)
                });
            });

            return edges;
        }

        const convertNodes = (nodes: GraphNode[]) : DataSet<Node, 'id'> => {
            const networkNodes = new DataSet<Node, 'id'>();
            nodes.forEach((node) => {
                networkNodes.add({
                    ...nodeProps,
                    id: node.id,
                    label: node.script.name,
                    color: {
                        background: node.valid ? theme.blue_bg : theme.invalid_bg,
                        border: node.valid ? theme.apm_blue : theme.invalid_fg,
                    },
                    font: {
                        color: node.valid ? theme.apm_blue : theme.invalid_fg,
                    },
                    title: node.script.path
                });
            })

            return networkNodes;
        }

        const nodes = graphData?.nodes;
        const transitions = graphData?.transitions;
        if (nodes && transitions && null !== container.current) {
            const networkNodes = convertNodes(nodes);
            const edges = convertToEdges(transitions);
            const dependentNodes: DataSet<Node, 'id'> = new DataSet<Node, 'id'>();

            networkNodes.forEach((item: Node) => {
                const exist = edges.getDataSet().getIds().find((id) => {
                    return item.id !== undefined ? id.toString().indexOf(item.id.toString()) !== -1 : false
                });
                if (exist) {
                    dependentNodes.add(item)
                }
            });

            const networkOptions = {
                physics: false,
                edges: {arrows: 'to'},
                layout: {hierarchical: {nodeSpacing: 300, treeSpacing: 300, sortMethod: 'directed'}}
            };

            const network = new Network(container.current, {nodes: dependentNodes, edges}, networkOptions);
            network.on('doubleClick', (e) => handleDoubleClick(e, nodes));
        }
    }, [graphData])



    const handleDoubleClick = (e: any, nodes: GraphNode[]) => {
        const selectedNode: GraphNode | undefined = nodes.find((node) => node.id === e.nodes[0]);
        window.open(`${window.location.origin}/apm/editor.html${selectedNode?.script.path}`, '_blank');
    };

    const nodeProps = {
        shape: 'box',
        size: 30,
        margin: {
            top: 10,
            bottom: 10,
            right: 10,
            left: 10
        },
        borderWidth: theme.width,
        font: {
            size: 16
        }
    };

    return <>
        <div ref={container} id="graph"/>
    </>
};