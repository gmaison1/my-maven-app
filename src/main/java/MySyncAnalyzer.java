import org.objectweb.asm.*;
import java.io.*;

public class MySyncAnalyzer extends ClassVisitor {
    private int totalInstructions = 0;
    private int protectedInstructions = 0;
    
    public MySyncAnalyzer() {
        super(Opcodes.ASM9);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        return new SyncMethodVisitor(this);
    }
    
    public void addTotalInstructions(int count) {
        totalInstructions += count;
    }
    
    public void addProtectedInstructions(int count) {
        protectedInstructions += count;
    }
    
    public void printResults() {
        double percentage = totalInstructions > 0 
            ? (protectedInstructions * 100.0 / totalInstructions) 
            : 0.0;
        System.out.printf("%d   %d   %.0f%%\n", 
            totalInstructions, protectedInstructions, percentage);
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java MySyncAnalyzer <classfile>");
            System.exit(1);
        }
        
        try {
            FileInputStream fis = new FileInputStream(args[0]);
            ClassReader cr = new ClassReader(fis);
            MySyncAnalyzer analyzer = new MySyncAnalyzer();
            cr.accept(analyzer, 0);
            analyzer.printResults();
            fis.close();
        } catch (IOException e) {
            System.err.println("Error reading class file: " + e.getMessage());
            System.exit(1);
        }
    }
}

class SyncMethodVisitor extends MethodVisitor {
    private MySyncAnalyzer analyzer;
    private int instructionCount = 0;
    private int monitorDepth = 0;  // Track nesting level
    private int protectedCount = 0;
    
    public SyncMethodVisitor(MySyncAnalyzer analyzer) {
        super(Opcodes.ASM9);
        this.analyzer = analyzer;
    }
    
    @Override
    public void visitInsn(int opcode) {
        instructionCount++;
        
        if (opcode == Opcodes.MONITORENTER) {
            monitorDepth++;
            protectedCount++;  // Count the monitorenter itself
        } else if (opcode == Opcodes.MONITOREXIT) {
            protectedCount++;  // Count the monitorexit itself
            monitorDepth--;
        } else if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitIntInsn(int opcode, int operand) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitVarInsn(int opcode, int var) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitTypeInsn(int opcode, String type) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, 
                                String descriptor, boolean isInterface) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitJumpInsn(int opcode, Label label) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitLdcInsn(Object value) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitIincInsn(int var, int increment) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        instructionCount++;
        if (monitorDepth > 0) {
            protectedCount++;
        }
    }
    
    @Override
    public void visitEnd() {
        analyzer.addTotalInstructions(instructionCount);
        analyzer.addProtectedInstructions(protectedCount);
    }
}
