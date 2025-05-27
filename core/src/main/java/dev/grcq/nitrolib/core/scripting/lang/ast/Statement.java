package dev.grcq.nitrolib.core.scripting.lang.ast;

import lombok.Getter;

public interface Statement extends ASTNode {

    @Getter
    class Block implements Statement {
        private final Statement[] statements;

        public Block(Statement[] statements) {
            this.statements = statements;
        }

        @Override
        public String toString() {
            return "Block{" +
                    "statements=" + java.util.Arrays.toString(statements) +
                    '}';
        }
    }

}
