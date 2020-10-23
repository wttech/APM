import React, {useState} from 'react';
import {GraphNode} from "./GraphView";

const useToggle = (initialValue = false) => {
  const [toggled, setToggled] = useState(initialValue);
  const toggle = () => setToggled(!toggled);
  return [toggled, toggle] as const;
};

const TreeNodeComponent: React.FC<GraphNode> = ({script, children}) => {
  const [expanded, toggleExpanded] = useToggle(true);
  console.log(children);

/*
  const childrenMarkup = children?.map((child, key) => {
    return <li><TreeNodeComponent key={key} script={child.script} children={child.children}/></li>
  });
*/

  return <>
 {/*   {!!children?.length && <button onClick={toggleExpanded}>{expanded ? '-' : '+'} </button>}
    {script && script.name}
    {expanded &&
    <ul>
      {childrenMarkup}
    </ul>}*/}
  </>
};

export default TreeNodeComponent;