import React from 'react';

type GraphProps = {
  title: string,
  paragraph: string
}

export const Graph = ({ title, paragraph }: GraphProps) => <aside>
  <h2>{ title }</h2>
  <p>
    { paragraph }
  </p>
</aside>;
