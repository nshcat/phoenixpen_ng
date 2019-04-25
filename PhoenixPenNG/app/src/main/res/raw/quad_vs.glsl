#version 310 es

precision mediump float;

const vec2 vertex_offset[] = vec2[6](
    vec2(1, 1),	    // BR
    vec2(1, -1),	// TR
    vec2(-1, -1),	// TL

    vec2(1, 1),	    // BR
    vec2(-1, -1),	// TL
    vec2(-1, 1)	    // BL
);

// Calculate vertex for this shader call
void emit_vertex()
{
    vec2 pos =  vertex_offset[gl_VertexID];

    gl_Position = vec4(pos, 0.f, 1.f);
}

void main()
{
    emit_vertex();
}